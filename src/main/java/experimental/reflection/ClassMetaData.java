package experimental.reflection;

public class ClassMetaData {
    //public class FooContainer {
    //
    //    private Foo<String> item;
    //
    //    @Getter
    //    @ToString
    //    public static class Foo<T> {
    //        private T fooValue;
    //        private Object otherFooValue;
    //    }

    // PClass { ClassMetaData; item[String]
    // PClass { Foo; fooValue[T], otherFooValue[Object]

    private PClass pClass;

    public ClassMetaData(PClass pClass) {
        this.pClass = pClass;
    }


}
