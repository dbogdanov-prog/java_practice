import java.util.ArrayList;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    record Course(String code, int duration) {}

    record DurationStats(long count, long sum, int min, int max) {
        DurationStats {
            if (count < 0) {
                throw new IllegalArgumentException("count must not be negative");
            }
            if (count == 0 && (sum != 0 || min != 0 || max != 0)) {
                throw new IllegalArgumentException("empty statistics must be all zeros");
            }
            if (count > 0 && min > max) {
                throw new IllegalArgumentException("min must not exceed max");
            }
        }

        static final DurationStats EMPTY = new DurationStats(0, 0, 0, 0);

        OptionalInt minimum() {
            return count == 0 ? OptionalInt.empty() : OptionalInt.of(min);
        }

        OptionalInt maximum() {
            return count == 0 ? OptionalInt.empty() : OptionalInt.of(max);
        }

        OptionalDouble average() {
            return count == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) sum / count);
        }
    }

    static final class Accumulator {
        private long count;
        private long sum;
        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;

        void add(int value) {
            count++;
            sum += value;
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }

        Accumulator merge(Accumulator other) {
            count += other.count;
            sum += other.sum;
            if (other.count > 0) {
                min = Math.min(min, other.min);
                max = Math.max(max, other.max);
            }
            return this;
        }

        DurationStats finish() {
            return count == 0 ? DurationStats.EMPTY : new DurationStats(count, sum, min, max);
        }
    }

    static <T> Collector<T, Accumulator, DurationStats> durationStats(ToIntFunction<? super T> mapper) {
        return Collector.of(
                Accumulator::new,
                (acc, item) -> acc.add(mapper.applyAsInt(item)),
                Accumulator::merge,
                Accumulator::finish,
                Collector.Characteristics.UNORDERED);
    }

    static boolean everySplitAgrees(List<Course> courses) {
        Collector<Course, Accumulator, DurationStats> collector = durationStats(Course::duration);
        DurationStats whole = courses.stream().collect(collector);
        for (int cut = 0; cut <= courses.size(); cut++) {
            Accumulator left = collector.supplier().get();
            Accumulator right = collector.supplier().get();
            courses.subList(0, cut).forEach(c -> collector.accumulator().accept(left, c));
            courses.subList(cut, courses.size()).forEach(c -> collector.accumulator().accept(right, c));
            if (!collector.finisher().apply(collector.combiner().apply(left, right)).equals(whole)) {
                return false;
            }
        }
        return true;
    }

    static boolean sameAsBuiltIn(DurationStats mine, IntSummaryStatistics builtIn) {
        return mine.count() == builtIn.getCount()
                && mine.sum() == builtIn.getSum()
                && mine.min() == builtIn.getMin()
                && mine.max() == builtIn.getMax()
                && mine.average().orElse(0.0) == builtIn.getAverage();
    }

    static List<Course> data() {
        return List.of(
                new Course("JV-101", 40),
                new Course("JV-201", 36),
                new Course("SP-220", 48),
                new Course("DB-230", 30),
                new Course("GT-100", 12),
                new Course("AR-340", 72),
                new Course("JV-301", 60));
    }

    public static void main(String[] args) {
        List<Course> courses = data();

        DurationStats mine = courses.stream().collect(durationStats(Course::duration));
        IntSummaryStatistics builtIn = courses.stream().collect(Collectors.summarizingInt(Course::duration));

        System.out.println("custom collector : " + mine);
        System.out.println("built-in          : " + builtIn);
        System.out.println("equal to built-in on non-empty data: " + sameAsBuiltIn(mine, builtIn));

        System.out.println();
        DurationStats emptyMine = List.<Course>of().stream().collect(durationStats(Course::duration));
        IntSummaryStatistics emptyBuiltIn = List.<Course>of().stream().collect(Collectors.summarizingInt(Course::duration));
        System.out.println("empty, custom  : " + emptyMine + " min=" + emptyMine.minimum() + " avg=" + emptyMine.average());
        System.out.println("empty, built-in: min=" + emptyBuiltIn.getMin() + " max=" + emptyBuiltIn.getMax() + " avg=" + emptyBuiltIn.getAverage());

        System.out.println();
        System.out.println("combiner agrees for every split point: " + everySplitAgrees(courses));

        boolean shuffledSame = true;
        for (int seed = 0; seed < 200; seed++) {
            List<Course> shuffled = new ArrayList<>(courses);
            Collections.shuffle(shuffled, new Random(seed));
            shuffledSame &= shuffled.stream().collect(durationStats(Course::duration)).equals(mine);
        }
        System.out.println("same result for 200 shuffled inputs  : " + shuffledSame);

        List<Course> many = IntStream.range(0, 1_000_000)
                .mapToObj(i -> new Course("C" + i, 1 + (i * 31) % 97))
                .toList();
        DurationStats sequential = many.stream().collect(durationStats(Course::duration));
        DurationStats parallel = many.parallelStream().collect(durationStats(Course::duration));
        IntSummaryStatistics parallelBuiltIn = many.parallelStream().collect(Collectors.summarizingInt(Course::duration));
        System.out.println("1,000,000 elements, sequential == parallel: " + sequential.equals(parallel));
        System.out.println("parallel custom equals parallel built-in  : " + sameAsBuiltIn(parallel, parallelBuiltIn));

        System.out.println();
        builtIn.accept(1000);
        System.out.println("built-in object can be mutated afterwards : count=" + builtIn.getCount() + " max=" + builtIn.getMax());
        System.out.println("custom result is a record, no mutators    : " + mine.getClass().isRecord());
        System.out.println("collector characteristics                 : "
                + durationStats((Course c) -> c.duration()).characteristics());
    }
}
