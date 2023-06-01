package com.asecave.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
	
	private SpriteBatch batch;
	private FrameBuffer fbo1;
	private FrameBuffer fbo2;
	private OrthographicCamera cam;
	private ShaderProgram shader;
	private ShaderProgram render;
	
	private int width;
	private int height;
	
	private final float scale = 1f;
	
	@Override
	public void create () {
		
		width = (int) (Gdx.graphics.getWidth() / scale);
		height = (int) (Gdx.graphics.getHeight() / scale);
		
		batch = new SpriteBatch();
		
		fbo1 = new FrameBuffer(Format.RGB888, width, height, false);
		fbo2 = new FrameBuffer(Format.RGB888, width, height, false);
		
		Pixmap pix = new Pixmap(width, height, Format.RGB888);
		pix.setColor(Color.GREEN);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pix.drawPixel(x, y);
			}
		}
		pix.setColor(Color.BLUE);
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				if (Math.random() < 0.00001f) {
//					pix.drawCircle(x, y, (int) (Math.random() * 200));
//				}
//			}
//		}
		pix.fillCircle(width / 2, height / 2, 10);
		
		fbo1.begin();
		batch.begin();
		batch.draw(new Texture(pix), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		fbo1.end();
		
		cam = new OrthographicCamera();
		
		cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0f);
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vert"), Gdx.files.internal("shaders/screen.frag"));
		if (!shader.isCompiled()) {
			System.out.println(shader.getLog());
		}
		render = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vert"), Gdx.files.internal("shaders/render.frag"));
		if (!render.isCompiled()) {
			System.out.println(render.getLog());
		}
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		
		cam.update();
		
		shader.bind();
		shader.setUniformf("frameDimensions", new Vector2(width, height));
		
		batch.setProjectionMatrix(cam.combined);
		batch.setShader(shader);
		fbo2.begin();
		batch.begin();
		batch.draw(fbo1.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		fbo2.end();
		
		batch.setShader(null);
		fbo1.begin();
		batch.begin();
		batch.draw(fbo2.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		fbo1.end();

		batch.setShader(render);
		batch.begin();
		fbo2.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		batch.draw(fbo2.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = width;
		cam.viewportHeight = height;
	}
}
