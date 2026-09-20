
calc = new Calculator();
x = calc.add(6, -2);
x = calc.multiply(4, 1);
x = calc.subtract(20, 16);
x = calc.square(2);
x = calc.isOdd(17);
x = calc.isOdd(18);
x = calc.max(-4, 4);
x = calc.max(4, -4);
x = calc.abs(-4);
x = calc.abs(4);

box = new Box(4);
x = box.getValue(); //x is 4
y = box.getValue(); //y is 4
box.setValue(2);
x = box.getValue(); //x is 2

counter = new Counter();
counter.tick();
counter.tick();
counter.tick();
x = counter.getCount(); //x is 3
counter.tick();
counter.tick();
x = counter.getCount(); //x is 5
counter.reset();
counter.tick();
counter.tick();
x = counter.getCount(); //x is 2

flip = new Flipper();
x = flip.next(); //x is true
x = flip.next(); //x is false
x = flip.next(); //x is true
x = flip.next(); //x is false

fib = new Fibber();
x = fib.next(); //x is 1
x = fib.next(); //x is 1
x = fib.next(); //x is 2
x = fib.next(); //x is 3

c1 = new Container(1);
c2 = new Container(2);
swapper = new Swapper();
swapper.swap(c1, c2);
x = c1.getStuff();
x = c2.getStuff();
// x is 2
// x is 1

e = new Enumerator(3, 5);
x = e.hasNext();
x = e.next();
x = e.next();
x = e.hasNext();
x = e.next();
x = e.hasNext();

