#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

// Propiedades del material
uniform bool useTexture;
uniform bool useLighting;
uniform sampler2D textureSampler;
uniform vec3 objectColor;

// Propiedades de la luz
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 viewPos;

void main()
{
    vec3 color;
    
    // Obtener color base
    if (useTexture) {
        color = texture(textureSampler, TexCoords).rgb;
    } else {
        color = objectColor;
    }
    
    if (useLighting) {
        // Ambient
        float ambientStrength = 0.2;
        vec3 ambient = ambientStrength * lightColor;
        
        // Diffuse 
        vec3 norm = normalize(Normal);
        vec3 lightDir = normalize(lightPos - FragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * lightColor;
        
        // Specular
        float specularStrength = 0.5;
        vec3 viewDir = normalize(viewPos - FragPos);
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
        vec3 specular = specularStrength * spec * lightColor;
        
        // Combinar resultados
        vec3 result = (ambient + diffuse + specular) * color;
        FragColor = vec4(result, 1.0);
    } else {
        FragColor = vec4(color, 1.0);
    }
}