import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    record Course(String code, int duration) {}

    record Load(int courses, int hours) {
        static final Load IDENTITY = new Load(0, 0);
        static final Load WRONG_IDENTITY = new Load(1, 10);

        static Load of(Course course) {
            return new Load(1, course.duration());
        }

        Load plus(Load other) {
            return new Load(Math.addExact(courses, other.courses), Math.addExact(hours, other.hours));
        }
    }

    static <T, R> R foldInParts(List<T> items, int parts, R identity,
                                BiFunction<R, T, R> accumulator, BinaryOperator<R> combiner) {
        int size = items.size();
        R result = null;
        for (int part = 0; part < parts; part++) {
            int from = (int) ((long) size * part / parts);
            int to = (int) ((long) size * (part + 1) / parts);
            R partial = identity;
            for (T item : items.subList(from, to)) {
                partial = accumulator.apply(partial, item);
            }
            result = result == null ? partial : combiner.apply(result, partial);
        }
        return result;
    }

    static boolean isAssociative(List<Load> samples, BinaryOperator<Load> op) {
        for (Load a : samples) {
            for (Load b : samples) {
                for (Load c : samples) {
                    if (!op.apply(op.apply(a, b), c).equals(op.apply(a, op.apply(b, c)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static boolean isIdentity(List<Load> samples, Load identity, BinaryOperator<Load> op) {
        for (Load a : samples) {
            if (!op.apply(identity, a).equals(a) || !op.apply(a, identity).equals(a)) {
                return false;
            }
        }
        return true;
    }

    static List<Course> data() {
        return List.of(
                new Course("JV-101", 40),
                new Course("JV-201", 36),
                new Course("JV-210", 36),
                new Course("SP-220", 48),
                new Course("DB-230", 30),
                new Course("GT-100", 12),
                new Course("AR-340", 72),
                new Course("JV-301", 60));
    }

    public static void main(String[] args) {
        List<Course> courses = data();
        int expected = courses.stream().mapToInt(Course::duration).sum();

        System.out.println("Sum of durations");
        System.out.println("mapToInt.sum()              : " + expected);
        System.out.println("reduce(0, Integer::sum)     : " + courses.stream().map(Course::duration).reduce(0, Integer::sum));
        System.out.println("reduce(1, Integer::sum)     : " + courses.stream().map(Course::duration).reduce(1, Integer::sum));
        System.out.println("reduce(0, ...) on empty list: " + List.<Course>of().stream().map(Course::duration).reduce(0, Integer::sum));
        System.out.println("reduce(100, ...) on empty   : " + List.<Course>of().stream().map(Course::duration).reduce(100, Integer::sum));

        System.out.println();
        System.out.println("Value object Load(courses, hours)");
        BiFunction<Load, Course, Load> accumulator = (load, course) -> load.plus(Load.of(course));
        BinaryOperator<Load> combiner = Load::plus;
        Load good = courses.stream().reduce(Load.IDENTITY, accumulator, combiner);
        System.out.println("correct identity: " + good);
        System.out.println("empty list, correct identity: " + List.<Course>of().stream().reduce(Load.IDENTITY, accumulator, combiner));
        System.out.println("empty list, wrong identity  : " + List.<Course>of().stream().reduce(Load.WRONG_IDENTITY, accumulator, combiner));

        System.out.println();
        System.out.println("Deterministic simulation of partitioning (parts -> result)");
        for (int parts : new int[] {1, 2, 4, 8}) {
            Load withGood = foldInParts(courses, parts, Load.IDENTITY, accumulator, combiner);
            Load withBad = foldInParts(courses, parts, Load.WRONG_IDENTITY, accumulator, combiner);
            System.out.printf("parts=%d  correct=%s  wrong=%s  correct==expected: %b  wrong==expected: %b%n",
                    parts, withGood, withBad, withGood.equals(good), withBad.equals(good));
        }

        List<Course> many = IntStream.range(0, 100_000)
                .mapToObj(i -> new Course("C" + i, 1 + i % 7))
                .collect(Collectors.toCollection(ArrayList::new));
        Load sequentialGood = many.stream().reduce(Load.IDENTITY, accumulator, combiner);
        Load parallelGood = many.parallelStream().reduce(Load.IDENTITY, accumulator, combiner);
        Load sequentialBad = many.stream().reduce(Load.WRONG_IDENTITY, accumulator, combiner);
        Load parallelBad = many.parallelStream().reduce(Load.WRONG_IDENTITY, accumulator, combiner);
        System.out.println();
        System.out.println("100000 courses, real parallel stream");
        System.out.println("correct identity: sequential=" + sequentialGood + " parallel=" + parallelGood);
        System.out.println("wrong identity  : sequential=" + sequentialBad + " parallel=" + parallelBad);

        List<Load> samples = courses.stream().map(Load::of).toList();
        BinaryOperator<Load> minus = (a, b) -> new Load(a.courses() - b.courses(), a.hours() - b.hours());

        System.out.println();
        System.out.println("Algebraic laws of the accumulator");
        System.out.println("plus is associative            : " + isAssociative(samples, Load::plus));
        System.out.println("IDENTITY is neutral for plus   : " + isIdentity(samples, Load.IDENTITY, Load::plus));
        System.out.println("WRONG_IDENTITY is neutral      : " + isIdentity(samples, Load.WRONG_IDENTITY, Load::plus));
        System.out.println("minus is associative           : " + isAssociative(samples, minus));

        Load sequentialMinus = courses.stream().map(Load::of).reduce(Load.IDENTITY, minus);
        Load partitionedMinus = foldInParts(courses, 4, Load.IDENTITY, (l, c) -> minus.apply(l, Load.of(c)), minus);
        System.out.println("minus sequential : " + sequentialMinus);
        System.out.println("minus in 4 parts : " + partitionedMinus);

        boolean combinerCompatible = true;
        for (Load u : samples) {
            for (Course t : courses) {
                combinerCompatible &= combiner.apply(u, accumulator.apply(Load.IDENTITY, t)).equals(accumulator.apply(u, t));
            }
        }
        System.out.println("combiner(u, acc(identity, t)) == acc(u, t): " + combinerCompatible);
    }
}
