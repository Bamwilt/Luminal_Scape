package main;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.joml.Matrix4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class Wall {

    // Buffers
    private int vao;
    private int vbo;
    private int ebo;

    // Propiedades
    private int textureID;
    private boolean hasTexture;
    private boolean hasLighting;
    private float[] color;
    private float width;
    private float height;

    // Transformaciones
    private Matrix4f modelMatrix;
    private FloatBuffer matrixBuffer;

    // Textura
    private float textureScaleX = 1.0f;
    private float textureScaleY = 1.0f;

    // Constructor con textura
    public Wall(float width, float height, int textureID, boolean withLighting) {
        init(width, height, new float[]{1.0f, 1.0f, 0.0f, 1.0f}, withLighting);
        this.textureID = textureID;
        this.hasTexture = true;
    }

    // Constructor con color
    public Wall(float width, float height, float[] color, boolean withLighting) {
        init(width, height, color != null ? color : new float[]{1.0f, 1.0f, 0.0f, 1.0f}, withLighting);
        this.textureID = 0;
        this.hasTexture = false;
    }

    // Constructor simplificado
    public Wall(float width, float height) {
        this(width, height, null, false);
    }

    private void init(float width, float height, float[] color, boolean withLighting) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.hasLighting = withLighting;
        this.modelMatrix = new Matrix4f().identity();
        this.matrixBuffer = BufferUtils.createFloatBuffer(16);
        setupMesh();
    }

    private void setupMesh() {
        float w = width / 2.0f;
        float h = height / 2.0f;

        // Vertex data: posición, normal, coordenadas UV
        float[] vertices = {
            // Positions          // Normals         // Texture Coords
            -w, -h, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            w, -h, 0.0f, 0.0f, 0.0f, 1.0f, textureScaleX, 0.0f,
            w, h, 0.0f, 0.0f, 0.0f, 1.0f, textureScaleX, textureScaleY,
            -w, h, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, textureScaleY
        };

        int[] indices = {0, 1, 2, 2, 3, 0};

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // EBO
        ebo = glGenBuffers();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // Atributos
        // Posición
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Normal
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // UV
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    public void render(Shader shader) {
        // Configurar matriz de modelo
        modelMatrix.get(matrixBuffer);
        shader.setMat4("model", matrixBuffer);

        // Configurar textura
        if (hasTexture) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureID);
            shader.setInt("textureSampler", 0);
            shader.setBool("useTexture", true);
        } else {
            shader.setVec4("objectColor", color[0], color[1], color[2], color[3]);
            shader.setBool("useTexture", false);
        }

        // Configurar iluminación
        shader.setBool("useLighting", hasLighting);

        // Dibujar
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    // Métodos de transformación
    public void translate(float x, float y, float z) {
        modelMatrix.translate(x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        modelMatrix.rotate(angle, x, y, z);
    }

    public void scale(float x, float y, float z) {
        modelMatrix.scale(x, y, z);
    }

    // Getters y setters mejorados
    public void setTexture(int textureID, float scaleX, float scaleY) {
        this.textureID = textureID;
        this.hasTexture = true;
        this.textureScaleX = scaleX;
        this.textureScaleY = scaleY;
        updateTextureCoords();
    }

    public void setColor(float[] color) {
        this.color = color;
        this.hasTexture = false;
    }

    public void setLightingEnabled(boolean enabled) {
        this.hasLighting = enabled;
    }

    public void setTextureScale(float scaleX, float scaleY) {
        this.textureScaleX = scaleX;
        this.textureScaleY = scaleY;
        updateTextureCoords();
    }

    public void setRotation(float angle, float x, float y, float z) {
        modelMatrix.rotate(angle, x, y, z);
    }

    private void updateTextureCoords() {
        if (!hasTexture) {
            return;
        }

        float w = width / 2.0f;
        float h = height / 2.0f;

        float[] uvData = {
            0.0f, 0.0f,
            textureScaleX, 0.0f,
            textureScaleX, textureScaleY,
            0.0f, textureScaleY
        };

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 6 * Float.BYTES * 4, uvData);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
