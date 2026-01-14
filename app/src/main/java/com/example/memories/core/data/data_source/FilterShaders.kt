package com.example.memories.core.data.data_source

object FilterShaders {
    const val GRAYSCALE_SHADER = """
        uniform shader inputShader;
        half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            float gray = dot(color.rgb, float3(0.299, 0.587, 0.114));
            return half4(gray, gray, gray, color.a);
        }
    """

    const val SEPIA_SHADER = """
        uniform shader inputShader;
        half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            float r = dot(color.rgb, float3(0.393, 0.769, 0.189));
            float g = dot(color.rgb, float3(0.349, 0.686, 0.168));
            float b = dot(color.rgb, float3(0.272, 0.534, 0.131));
            return half4(r, g, b, color.a);
        }
    """

    const val INVERT_SHADER = """
        uniform shader inputShader;
        half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            return half4(1.0 - color.rgb, color.a);
        }
    """

    const val ADEN_SHADER = """
    uniform shader inputShader;
    half4 main(float2 fragCoord) {
    half4 color = inputShader.eval(fragCoord);

    // ----- 1. Lift blacks / reduce contrast -----
    color.rgb = (color.rgb - 0.5) * 0.85 + 0.55;

    // ----- 2. Warm tone (Aden warmth) -----
    color.r += 0.05;
    color.g += 0.02;
    color.b -= 0.03;

    // ----- 3. Soft pink highlight tint -----
    half luminance = dot(color.rgb, half3(0.299, 0.587, 0.114));
    half3 pinkTint = half3(1.0, 0.9, 0.95);
    color.rgb = mix(color.rgb, color.rgb * pinkTint, luminance * 0.25);

    // ----- 4. Slight desaturation -----
    half gray = dot(color.rgb, half3(0.299, 0.587, 0.114));
    color.rgb = mix(half3(gray), color.rgb, 0.9);

    return color;
}
    """

    const val VINTAGE_SHADER = """
        uniform shader inputShader;

half4 main(float2 fragCoord) {
    half4 c = inputShader.eval(fragCoord);
    c.rgb *= half3(1.1, 1.0, 0.9);
    c.rgb = (c.rgb - 0.5) * 0.9 + 0.5;
    return c;
}
    """

    const val COOL_FADE_SHADER = """
        uniform shader inputShader;

half4 main(float2 fragCoord) {
    half4 c = inputShader.eval(fragCoord);
    c.rgb = mix(c.rgb, half3(0.7, 0.85, 1.0), 0.2);
    return c;
}

    """


    const val BRIGHTNESS_SHADER = """
        uniform shader inputShader;
        uniform float brightness; 

    half4 main(float2 fragCoord) {
    half4 color = inputShader.eval(fragCoord);
    half b = clamp(brightness / 100.0, -1.0, 1.0);
    color.rgb += b;
    return color;
}
    """

    const val BLUR_SHADER = """
        uniform shader inputShader;
uniform float radius;   // expected range: 0.0 – 20.0

half4 main(float2 fragCoord) {
    half4 color = half4(0.0);
    int samples = 9;

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            float2 offset = float2(x, y) * radius;
            color += inputShader.eval(fragCoord + offset);
        }
    }

    return color / samples;
}
    """

}