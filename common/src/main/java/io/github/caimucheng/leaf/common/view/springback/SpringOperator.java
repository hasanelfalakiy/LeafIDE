package io.github.caimucheng.leaf.common.view.springback;

public class SpringOperator {
    private final double damping;
    private final double tension;

    public SpringOperator(float f, float f2) {
        this.tension = Math.pow(6.283185307179586d / (double) f2, 2.0d);
        this.damping = (((double) f) * 12.566370614359172d) / (double) f2;
    }

    public double updateVelocity(double d, float f, double d2, double d3) {
        return (d * (1.0d - (this.damping * (double) f))) + ((double) ((float) (this.tension * (d2 - d3) * (double) f)));
    }
}