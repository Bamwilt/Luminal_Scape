package main;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private int vao, vbo, ebo;
    private int vertexCount;
    private int textureID = 0;
    private int shaderProgram;

    private Matrix4f modelMatrix = new Matrix4f().identity();

    public Mesh(float[] vertices, int[] indices, int shaderProgram) {
        this.shaderProgram = shaderProgram;
        vertexCount = indices.length;

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int stride = 5 * Float.BYTES; // 3 pos + 2 uv

        // Posición (x, y, z)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        // UV (s, t)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    Mesh(float[] vertices, int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setTexture(int textureID) {
        this.textureID = textureID;
    }

    // Para setear la posición absoluta (no acumular)
    public void setPosition(float x, float y, float z) {
        modelMatrix.m30(x);
        modelMatrix.m31(y);
        modelMatrix.m32(z);
    }

    // Para escalar absoluto (resetear y escalar)
    public void setScale(float sx, float sy, float sz) {
        // Ojo: Aquí resetea la matriz para evitar acumulación incorrecta
        modelMatrix.identity().scale(sx, sy, sz);
    }

    // Para rotar (resetea y rota)
    public void setRotation(float angleDeg, Vector3f axis) {
        modelMatrix.identity().rotate((float) Math.toRadians(angleDeg), axis);
    }

    public void resetTransform() {
        modelMatrix.identity();
    }

    public void render(Matrix4f projection) {
        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        int locProj = glGetUniformLocation(shaderProgram, "proj");
        int locModel = glGetUniformLocation(shaderProgram, "model");

        glUniformMatrix4fv(locProj, false, projection.get(new float[16]));
        glUniformMatrix4fv(locModel, false, modelMatrix.get(new float[16]));

        if (textureID != 0) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureID);
            glUniform1i(glGetUniformLocation(shaderProgram, "tex"), 0);
        }

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glUseProgram(0);
    }



    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }
}
