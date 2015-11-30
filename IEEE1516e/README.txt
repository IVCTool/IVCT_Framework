The IEEE1516e project is only needed for compiling as we only want a dependency on the IEEE1516e interface and NOT on the concrete implementations of it.

When running the tests, the IEEE1516e-0.1.jar MUST NOT be included in the runtime classpath. The runtime classpath has to include the libraries from the concrete implementation e.g. Pitch pRTI.
To reference the project in the <project>.gradle files use 

dependencies {
...
compile		project(':IEEE1516e')
...
}
