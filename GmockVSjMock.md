# Basic Usages #

### jMock ###
```
final List mock = mock(List.class);

checking(new Expectations() {{
  oneOf (mock).get(0); will(returnValue("one"));
  allowing (mock).get(1); will(returnValue("two"));
  oneOf (mock).clear(); will(throwException(new RuntimeException()));
}});

someCodeThatInteractsWithMock(mock);
```

### Gmock ###
```
def mock = mock(List) {
  get(0).returns('one')
  get(1).returns('two').stub()
  clear().raises(RuntimeException)
}

play {
  someCodeThatInteractsWithMock(mock)
}
```


# Order Checking #

### jMock ###
```
final Sequence sequence = sequence("order");
final List one = mock(List.class, "one");
final List two = mock(List.class, "two");

checking(new Expectations() {{
  oneOf (one).add("one"); inSequence(sequence); will(returnValue(true));
  oneOf (two).add("two"); inSequence(sequence); will(returnValue(true));
}});

someCodeThatInteractsWithMocks(one, two);
```

### Gmock ###
```
def one = mock(List)
def two = mock(List)

ordered {
  one.add("one").returns(true)
  two.add("two").returns(true)
}

play {
  someCodeThatInteractsWithMocks(one, two)
}
```


# Times Verification and Argument Matchers #

### jMock ###
```
final List mock = mock(List.class);

checking(new Expectations() {{
  exactly(3).of (mock).clear();
  atLeast(1).of (mock).add(with(anything())); will(returnValue(true));
}});

someCodeThatInteractsWithMock(mock);
```

### Gmock ###
```
def mock = mock(List)

mock.clear().times(3)
mock.add(anything()).returns(true).atLeastOnce()

play {
  someCodeThatInteractsWithMock(mock)
}
```