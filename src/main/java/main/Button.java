package main;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Button {

    private float relX, relY; // posición relativa (0..1)
    private int x, y;         // posición en pixeles calculada
    private int width, height;
    private String text;
    private TextRender textRenderer;

    // OpenGL IDs
    private int vao;
    private int vbo;
    private int shaderProgram;
    private float[] colorText = {1f, 1f, 1f};
    private float[] colorButton = {0.1f, 0.2f, 0.3f};

    public Button(float relX, float relY, int width, int height, String text, TextRender textRenderer) {
        this.relX = relX;
        this.relY = relY;
        this.width = width;
        this.height = height;
        this.text = text;
        this.textRenderer = textRenderer;

        createShader();
        createBuffers();
    }

    private void createShader() {
        String vertexShaderSource = """
            #version 330 core
            layout(location = 0) in vec2 aPos;
            uniform vec2 screenSize;
            void main() {
                float x = aPos.x / screenSize.x * 2.0 - 1.0;
                float y = 1.0 - (aPos.y / screenSize.y * 2.0); // Invertir Y para OpenGL
                gl_Position = vec4(x, y, 0.0, 1.0);
            }
        """;

        String fragmentShaderSource = """
            #version 330 core
            uniform vec3 buttonColor;
            out vec4 FragColor;
            void main() {
                FragColor = vec4(buttonColor, 1.0);
            }
        """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkCompileErrors(shaderProgram, "PROGRAM");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void checkCompileErrors(int shader, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == 0) {
                System.err.println("ERROR::PROGRAM_LINKING_ERROR\n" + glGetProgramInfoLog(shader));
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == 0) {
                System.err.println("ERROR::SHADER_COMPILATION_ERROR of type: " + type + "\n" + glGetShaderInfoLog(shader));
            }
        }
    }

    private void createBuffers() {
        vao = GL30.glGenVertexArrays();
        vbo = glGenBuffers();

        GL30.glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // No llenamos buffers aún, se actualizarán en draw()
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);

        GL30.glBindVertexArray(0);
    }

    public void updatePosition(int windowWidth, int windowHeight) {
        x = (int) (relX * windowWidth);
        y = (int) (relY * windowHeight);
    }

    public void draw(int windowWidth, int windowHeight) {
        // Actualizar los vértices del rectángulo (en píxeles)
        float[] vertices = new float[]{
            x, y,
            x + width, y,
            x + width, y + height,
            x, y,
            x + width, y + height,
            x, y + height
        };

        // Subir datos a VBO
        GL30.glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        // Usar shader
        glUseProgram(shaderProgram);

        // Pasar tamaño pantalla uniforme
        int screenSizeLoc = glGetUniformLocation(shaderProgram, "screenSize");
        glUniform2f(screenSizeLoc, windowWidth, windowHeight);

        // Color
        int colorLoc = glGetUniformLocation(shaderProgram, "buttonColor");
        glUniform3f(colorLoc, colorButton[0], colorButton[1], colorButton[2]);

        // Dibujar
        glDrawArrays(GL_TRIANGLES, 0, 6);

        GL30.glBindVertexArray(0);
        textRenderer.renderer(text, x + (width * 0.5f) - (textRenderer.getTextWidth(text) / 2), y + height - (textRenderer.getTextHeight(text)), colorText[0], colorText[1], colorText[2]);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void drawBackground(int windowWidth, int windowHeight) {
        // Actualizar los vértices del rectángulo (en píxeles)
        float[] vertices = new float[]{
            x, y,
            x + width, y,
            x + width, y + height,
            x, y,
            x + width, y + height,
            x, y + height
        };

        // Subir datos a VBO
        GL30.glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

        // Usar shader
        glUseProgram(shaderProgram);

        // Pasar tamaño pantalla uniforme
        int screenSizeLoc = glGetUniformLocation(shaderProgram, "screenSize");
        glUniform2f(screenSizeLoc, windowWidth, windowHeight);

        // Color
        int colorLoc = glGetUniformLocation(shaderProgram, "buttonColor");
        glUniform3f(colorLoc, colorButton[0], colorButton[1], colorButton[2]);

        // Dibujar
        glDrawArrays(GL_TRIANGLES, 0, 6);

        GL30.glBindVertexArray(0);
    }

    public void drawText() {
        // Dibujar texto después de todo el fondo
        textRenderer.renderer(
                text,
                x + (width * 0.5f) - (textRenderer.getTextWidth(text)) / 2,
                y + height - (textRenderer.getTextHeight(text)),
                colorText[0],
                colorText[1],
                colorText[2]
        );
    }

    public void setColor(float r, float g, float b) {
        this.colorButton[0] = r;
        this.colorButton[1] = g;
        this.colorButton[2] = b;
    }
}
