package org.ravry.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int id;

    private String loadShader(String filePath) {
        try {
            return Files.readString(Path.of(filePath), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Shader(String vertexShaderFile, String fragmentShaderFile) {
        String vertexShaderSrc = loadShader(vertexShaderFile);
        String fragmentShaderSrc = loadShader(fragmentShaderFile);

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSrc);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSrc);
        glCompileShader(fragmentShader);

        id = glCreateProgram();
        glAttachShader(id, vertexShader);
        glAttachShader(id, fragmentShader);
        glLinkProgram(id);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public Shader use() {
        glUseProgram(id);
        return this;
    }

    public Shader setUniformMat4(String name, Matrix4f matrix)
    {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);
        glUniformMatrix4fv(glGetUniformLocation(id, name), false, matrixBuffer);
        return this;
    }

    public Shader setUniformVec3(String name, Vector3f vector) {
        FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(3);
        vector.get(vectorBuffer);
        glUniform3fv(glGetUniformLocation(id, name), vectorBuffer);
        return this;
    }

    public void unuse() {
        glUseProgram(0);
    }

    public void delete() {
        glDeleteProgram(id);
    }
}