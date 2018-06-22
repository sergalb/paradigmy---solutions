package expression;


import expression.exceptions.OverflowException;

public class CheckedMultiply extends BinaryOperation {
    public CheckedMultiply(TripleExpression firstArg, TripleExpression secondArg) {
        super(firstArg, secondArg);
    }

    @Override
    protected void check(int x, int y) throws OverflowException{
        if (x > 0 && y > 0 && Integer.MAX_VALUE / x < y) {
            throw new OverflowException();
        }
        if (x > 0 && y < 0 && Integer.MIN_VALUE / x > y) {
            throw new OverflowException();
        }
        if (x < 0 && y > 0 && Integer.MIN_VALUE / y > x) {
            throw new OverflowException();
        }
        if (x < 0 && y < 0 && Integer.MAX_VALUE / x > y) {
            throw new OverflowException();
        }
    }

    @Override
    protected int apply(int x, int y) throws OverflowException {
        check(x, y);
        return x*y;
    }
}
