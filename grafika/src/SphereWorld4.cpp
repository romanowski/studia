// SphereWorld.cpp
// OpenGL SuperBible
// New and improved (performance) sphere world
// Program by Richard S. Wright Jr.

#include <GLTools.h>
#include <GLShaderManager.h>
#include <GLFrustum.h>
#include <GLBatch.h>
#include <GLFrame.h>
#include <GLMatrixStack.h>
#include <GLGeometryTransform.h>
#include <StopWatch.h>

#include <math.h>
#include <stdio.h>

#ifdef __APPLE__
#include <glut/glut.h>
#else
#define FREEGLUT_STATIC
#include <GL/glut.h>
#endif

#define NUM_SPHERES 50
GLFrame spheres[NUM_SPHERES];

GLShaderManager shaderManager; // Shader Manager
GLMatrixStack modelViewMatrix; // Modelview Matrix
GLMatrixStack projectionMatrix; // Projection Matrix
GLFrustum viewFrustum; // View Frustum
GLGeometryTransform transformPipeline; // Geometry Transform Pipeline

GLTriangleBatch torusBatch;
GLBatch floorBatch;
GLTriangleBatch sphereBatch;
GLFrame cameraFrame;

GLuint textureID; //kamien

bool LoadTGATexture(const char *szFileName, GLenum minFilter, GLenum magFilter,
		GLenum wrapMode) {
	GLbyte *pBits;
	int nWidth, nHeight, nComponents;
	GLenum eFormat;

	// Read the texture bits
	pBits = gltReadTGABits(szFileName, &nWidth, &nHeight, &nComponents,
			&eFormat);
	if (pBits == NULL)
		return false;

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
	glTexImage2D(GL_TEXTURE_2D, 0, nComponents, nWidth, nHeight, 0, eFormat,
			GL_UNSIGNED_BYTE, pBits);

	free(pBits);

	if (minFilter == GL_LINEAR_MIPMAP_LINEAR
			|| minFilter == GL_LINEAR_MIPMAP_NEAREST
			|| minFilter == GL_NEAREST_MIPMAP_LINEAR
			|| minFilter == GL_NEAREST_MIPMAP_NEAREST)
		glGenerateMipmap(GL_TEXTURE_2D);

	return true;
}

//////////////////////////////////////////////////////////////////
// This function does any needed initialization on the rendering
// context. 
void SetupRC() {
	// Initialze Shader Manager
	shaderManager.InitializeStockShaders();

	glEnable(GL_DEPTH_TEST);

	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	glGenTextures(1, &textureID);
	glBindTexture(GL_TEXTURE_2D, textureID);
	LoadTGATexture("stone.tga", GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE);

	// This makes a torus
	gltMakeTorus(torusBatch, 0.4f, 0.15f, 30, 30);

	// This make a sphere
	gltMakeSphere(sphereBatch, 0.1f, 26, 13);

	floorBatch.Begin(GL_LINES, 324);
	for (GLfloat x = -20.0; x <= 20.0f; x += 0.5) {
		floorBatch.Vertex3f(x, -0.55f, 20.0f);
		floorBatch.Vertex3f(x, -0.55f, -20.0f);

		floorBatch.Vertex3f(20.0f, -0.55f, x);
		floorBatch.Vertex3f(-20.0f, -0.55f, x);
	}
	floorBatch.End();

	// Randomly place the spheres

}

///////////////////////////////////////////////////
// Screen changes size or is initialized
void ChangeSize(int nWidth, int nHeight) {
	glViewport(0, 0, nWidth, nHeight);

	// Create the projection matrix, and load it on the projection matrix stack
	viewFrustum.SetPerspective(35.0f, float(nWidth) / float(nHeight), 1.0f,
			100.0f);
	projectionMatrix.LoadMatrix(viewFrustum.GetProjectionMatrix());

	// Set the transformation pipeline to use the two matrix stacks
	transformPipeline.SetMatrixStacks(modelViewMatrix, projectionMatrix);
}

// Called to draw scene
void RenderScene(void) {
	// Color values
	static GLfloat vFloorColor[] = { 0.0f, 1.0f, 0.0f, 1.0f };
	static GLfloat vTorusColor[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	static GLfloat vSphereColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };

	// Time Based animation
	static CStopWatch rotTimer;
	float yRot = rotTimer.GetElapsedSeconds() * 60.0f;

	// Clear the color and depth buffers
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// Save the current modelview matrix (the identity matrix)
	modelViewMatrix.PushMatrix();

	M3DMatrix44f mCamera;
	cameraFrame.GetCameraMatrix(mCamera);
	modelViewMatrix.PushMatrix(mCamera);

	// Transform the light position into eye coordinates
	M3DVector4f vLightPos = { 0.0f, 10.0f, 5.0f, 1.0f };
	M3DVector4f vLightEyePos;
	m3dTransformVector4(vLightEyePos, vLightPos, mCamera);

	static GLfloat vWhite[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	glBindTexture(GL_TEXTURE_2D, textureID);
	shaderManager.UseStockShader(GLT_SHADER_TEXTURE_POINT_LIGHT_DIFF,
			transformPipeline.GetModelViewMatrix(),
			transformPipeline.GetProjectionMatrix(), vLightPos, vWhite, 0);
	floorBatch.Draw();

	// Draw the spinning Torus
	modelViewMatrix.Translate(0.0f, 0.0f, -2.5f);

	// Save the Translation
	modelViewMatrix.PushMatrix();

	// Apply a rotation and draw the torus
	modelViewMatrix.Rotate(yRot, 0.0f, 1.0f, 0.0f);
	shaderManager.UseStockShader(GLT_SHADER_POINT_LIGHT_DIFF,
			transformPipeline.GetModelViewMatrix(),
			transformPipeline.GetProjectionMatrix(), vLightEyePos, vTorusColor);
	torusBatch.Draw();
	modelViewMatrix.PopMatrix(); // "Erase" the Rotation from before

	// Apply another rotation, followed by a translation, then draw the sphere
	modelViewMatrix.Rotate(yRot * -2.0f, 0.0f, 1.0f, 0.0f);
	modelViewMatrix.Translate(0.8f, 0.0f, 0.0f);
	shaderManager.UseStockShader(GLT_SHADER_POINT_LIGHT_DIFF,
			transformPipeline.GetModelViewMatrix(),
			transformPipeline.GetProjectionMatrix(), vLightEyePos,
			vSphereColor);
	sphereBatch.Draw();

	// Restore the previous modleview matrix (the identity matrix)
	modelViewMatrix.PopMatrix();
	modelViewMatrix.PopMatrix();
	// Do the buffer Swap
	glutSwapBuffers();

	// Tell GLUT to do it again
	glutPostRedisplay();
}

// Respond to arrow keys by moving the camera frame of reference
void SpecialKeys(int key, int x, int y) {
	float linear = 0.1f;
	float angular = float(m3dDegToRad(5.0f));

	if (key == GLUT_KEY_UP)
		cameraFrame.MoveForward(linear);

	if (key == GLUT_KEY_DOWN)
		cameraFrame.MoveForward(-linear);

	if (key == GLUT_KEY_LEFT)
		cameraFrame.RotateWorld(angular, 0.0f, 1.0f, 0.0f);

	if (key == GLUT_KEY_RIGHT)
		cameraFrame.RotateWorld(-angular, 0.0f, 1.0f, 0.0f);
}

int main(int argc, char* argv[]) {
	gltSetWorkingDirectory(argv[0]);

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(800, 600);

	glutCreateWindow("OpenGL SphereWorld");

	glutSpecialFunc(SpecialKeys);
	glutReshapeFunc(ChangeSize);
	glutDisplayFunc(RenderScene);

	GLenum err = glewInit();
	if (GLEW_OK != err) {
		fprintf(stderr, "GLEW Error: %s\n", glewGetErrorString(err));
		return 1;
	}

	SetupRC();
	glutMainLoop();
	return 0;
}
