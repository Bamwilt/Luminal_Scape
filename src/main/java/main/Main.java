package main;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static Camera camera;
    private static Shader wallShader;
    private static Wall wall, wall2;
    private static float rotationAngleX = 0;
    private static float rotationAngleY = 0;
    private static float rotationAngleZ = 0;
    private static Vector3f cameraPosition = new Vector3f();

    public static void main(String[] args) {
        Window window = new Window(800, 600, "Main");
        window.init();
        camera = new Camera();

        TextRender textRenderer = new TextRender("fonts/Roboto-Bold.ttf", 28);
        textRenderer.setProjection(window.getWidth(), window.getHeight());

        Button boton = new Button(0.01f, 0.08f, 150, 50, "Presionar", textRenderer);
        boton.updatePosition(window.getWidth(), window.getHeight());
        
        // Crear shader para la pared
        wallShader = new Shader("shaders/wall_vertex.glsl", "shaders/wall_fragment.glsl");

        // Cargar textura y crear pared
        int wallTexture = TextureLoader.loadTexture("textures/backWall.png");
        wall = new Wall(10.0f, 5.0f, wallTexture, false);
        wall2 = new Wall(10.0f, 5.0f, wallTexture, false);

        wall.translate(0.0f, 0.0f, -10.0f);
        wall2.translate(5.0f, 0.0f, -5.0f);
        
        // Rotación inicial de la segunda pared (90 grados en X)
        wall2.setRotation((float)Math.toRadians(90), 1.0f, 0.0f, 0.0f);
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        window.setOnResizeCallback((w, h) -> {
            textRenderer.setProjection(w, h);
            boton.updatePosition(w, h);
        });

        window.loop(() -> {
            // Limpiar buffers
            GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            long windowHandle = window.getWindowHandle();

            // Velocidades de movimiento y rotación
            float moveSpeed = 0.1f;
            float rotationSpeed = 0.02f;

            // Movimiento de la cámara
            if (glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS) {
                camera.moveForward();
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS) {
                camera.moveBackward();
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_A) == GLFW_PRESS) {
                camera.turnLeft();
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_D) == GLFW_PRESS) {
                camera.turnRight();
            }
    

            // Rotación de la pared (usando teclas numéricas)
            if (glfwGetKey(windowHandle, GLFW_KEY_1) == GLFW_PRESS) {
                rotationAngleX += rotationSpeed;
                wall.setRotation(rotationAngleX, 1.0f, 0.0f, 0.0f);
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_2) == GLFW_PRESS) {
                rotationAngleY += rotationSpeed;
                wall.setRotation(rotationAngleY, 0.0f, 1.0f, 0.0f);
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_3) == GLFW_PRESS) {
                rotationAngleZ += rotationSpeed;
                wall.setRotation(rotationAngleZ, 0.0f, 0.0f, 1.0f);
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_4) == GLFW_PRESS) {
                rotationAngleX -= rotationSpeed;
                wall.setRotation(rotationAngleX, 1.0f, 0.0f, 0.0f);
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_5) == GLFW_PRESS) {
                rotationAngleY -= rotationSpeed;
                wall.setRotation(rotationAngleY, 0.0f, 1.0f, 0.0f);
            }
            if (glfwGetKey(windowHandle, GLFW_KEY_6) == GLFW_PRESS) {
                rotationAngleZ -= rotationSpeed;
                wall.setRotation(rotationAngleZ, 0.0f, 0.0f, 1.0f);
            }

            // Resetear rotaciones
            if (glfwGetKey(windowHandle, GLFW_KEY_R) == GLFW_PRESS) {
                rotationAngleX = 0;
                rotationAngleY = 0;
                rotationAngleZ = 0;
                wall.setRotation(0, 0, 0, 0);
            }

            // Configurar transformaciones para la pared
            try (MemoryStack stack = MemoryStack.stackPush()) {
                Matrix4f projection = new Matrix4f()
                        .perspective(
                                (float) Math.toRadians(45.0f),
                                (float) window.getWidth() / window.getHeight(),
                                0.1f,
                                100.0f
                        );

                Matrix4f view = camera.getViewMatrix();

                // Dibujar paredes
                wallShader.use();
                wallShader.setMat4("projection", projection);
                wallShader.setMat4("view", view);
                wall.render(wallShader);
                wall2.render(wallShader);
            }

            // Deshabilitar profundidad para elementos 2D
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            // Obtener posición de la cámara
              cameraPosition.set(camera.getPosition());

            // Renderizar información de ejes
            String axisInfo = String.format(
                "Posición: X:%.2f Y:%.2f Z:%.2f",
                Math.toDegrees(rotationAngleX),
                Math.toDegrees(rotationAngleY),
                Math.toDegrees(rotationAngleZ),
                cameraPosition.x,
                cameraPosition.y,
                cameraPosition.z
            );
            
       
            // Renderizar textos
            textRenderer.rendererRelativo("Luminal Scape", 0.01f, 0.95f, 0.1f, 1f, 1f);
            textRenderer.rendererRelativo(axisInfo, 0.01f, 0.85f, 0.05f, 1f, 1f);
            
            boton.draw(window.getWidth(), window.getHeight());

            // Restaurar prueba de profundidad
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        });

        // Limpieza de recursos
        wall.cleanup();
        wall2.cleanup();
        wallShader.cleanup();
        window.cleanup();
    }
}