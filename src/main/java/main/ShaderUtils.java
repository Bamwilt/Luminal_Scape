package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL33.*;

public class ShaderUtils {

    public static int createShaderProgram(String vertexPath, String fragmentPath) {
        String vertexSrc = readFile(vertexPath);
        String fragmentSrc = readFile(fragmentPath);

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);
        checkCompile(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSrc);
        glCompileShader(fragmentShader);
        checkCompile(fragmentShader, "FRAGMENT");

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkLink(program);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    private static String readFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo shader: " + path);
        }
    }

    private static void checkCompile(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error compilando shader " + type + ":\n" + glGetShaderInfoLog(shader));
        }
    }

    private static void checkLink(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error linkeando shader program:\n" + glGetProgramInfoLog(program));
        }
    }

    static int createBasicShader() {
        String vertexSrc = """
        #version 330 core
        layout(location = 0) in vec3 aPos;
        layout(location = 1) in vec3 aColor;

        out vec3 ourColor;

        uniform mat4 projection;
        uniform mat4 model;

        void main() {
            gl_Position = projection * model * vec4(aPos, 1.0);
            ourColor = aColor;
        }
        """;

        String fragmentSrc = """
        #version 330 core
        in vec3 ourColor;
        out vec4 FragColor;

        void main() {
            FragColor = vec4(ourColor, 1.0);
        }
        """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSrc);
        glCompileShader(vertexShader);
        checkCompile(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSrc);
        glCompileShader(fragmentShader);
        checkCompile(fragmentShader, "FRAGMENT");

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkLink(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return shaderProgram;
    }

        public static int compileShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompile(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompile(fragmentShader, "FRAGMENT");

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkProgramLink(program);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    private static void checkShaderCompile(int shader, String type) {
        int[] success = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, success);
        if (success[0] == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            System.err.println("SHADER COMPILATION ERROR (" + type + "):\n" + log);
        }
    }

    private static void checkProgramLink(int program) {
        int[] success = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, success);
        if (success[0] == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            System.err.println("PROGRAM LINKING ERROR:\n" + log);
        }
    }

}
