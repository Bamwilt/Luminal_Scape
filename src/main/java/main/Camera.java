package main;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f front;
    private final Vector3f up;
    private float yaw;
    private float speed;
    private final float turnSpeed;

    public Camera() {
        position = new Vector3f(0.0f, 0.0f, 3.0f);  // Mantenemos Y fijo
        front = new Vector3f(0.0f, 0.0f, -1.0f);
        up = new Vector3f(0.0f, 1.0f, 0.0f);
        yaw = -90.0f;
        speed = 0.5f;
        turnSpeed = 2.0f;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, new Vector3f(position).add(front), up);
    }

    public void setSpeed(float speed_) {
        speed = 0.5f + speed_;
    }

    public void moveForward() {
        position.add(new Vector3f(front).mul(speed));
    }

    public void moveBackward() {
        position.sub(new Vector3f(front).mul(speed));
    }
    
    public void moveLeft() {
        float leftX = -front.z;
        float leftZ = front.x;
        position.x += leftX * speed;
        position.z += leftZ * speed;
    }

    public void moveRight() {
        float rightX = front.z;
        float rightZ = -front.x;
        position.x += rightX * speed;
        position.z += rightZ * speed;  
    }

    public void turnLeft() {
        yaw -= turnSpeed;
        updateDirection();
    }

    public void turnRight() {
        yaw += turnSpeed;
        updateDirection();
    }

    private void updateDirection() {
        float rad = (float) Math.toRadians(yaw);
        front.x = (float) Math.cos(rad);
        front.z = (float) Math.sin(rad);
        front.normalize();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getFront() {
        return front;
    }
}
