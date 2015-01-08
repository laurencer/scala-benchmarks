# Scala Benchmarks

This project contains a collection of experiments to explore the use of Scala
language features and their impact on performance and usability.

## Union Benchmarks

[Benchmark implementation](/src/test/scala/com/rouesnel/scala/benchmarks/unions/Benchmark.scala)

[Results from my development machine](http://laurencer.github.io/scala-benchmarks/union/)

Custom algebraic data types are about as fast as raw pattern matching for union
types with more than 2 elements.

The Scalaz disjunction (\/) is slower but not significantly slower - although it
does become cumbersome to work with due to the nesting.

Shapeless written naively using a polymorphic map across a coproduct and then
a select is very slow (nearly an order of magnitude). If you give up totality
checking and simply try and select all the different cases then the performance
is a little worse than twice as slow.

