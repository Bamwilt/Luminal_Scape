package main;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram {

    private int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        int vertexId = compileShader(vertexPath, GL20.GL_VERTEX_SHADER);
        int fragmentId = compileShader(fragmentPath, GL20.GL_FRAGMENT_SHADER);

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexId);
        GL20.glAttachShader(programId, fragmentId);
        GL20.glLinkProgram(programId);

        GL20.glDeleteShader(vertexId);
        GL20.glDeleteShader(fragmentId);
    }

    private int compileShader(String path, int type) {
        InputStream stream = getClass().getResourceAsStream("/" + path);
        String code = new BufferedReader(new InputStreamReader(stream))
                .lines().reduce("", (acc, line) -> acc + line + "\n");

        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, code);
        GL20.glCompileShader(shaderId);

        return shaderId;
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public void setMat4(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            matrix.get(fb);
            GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(programId, name), false, fb);
        }
    }
}
