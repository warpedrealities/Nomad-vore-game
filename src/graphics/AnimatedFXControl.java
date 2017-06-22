package graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.util.vector.Vector4f;

import rendering.SquareRenderer;
import shared.Vec2f;

public class AnimatedFXControl {

	Queue<FX> visualEffects;
	FX currentVisualEffect;
	SquareRenderer[] m_squares;

	public AnimatedFXControl() {
		visualEffects = new LinkedList<FX>();

		m_squares = new SquareRenderer[2];
		m_squares[0] = new SquareRenderer(255, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));
		m_squares[1] = new SquareRenderer(254, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));
	}

	public void Update() {
		if (currentVisualEffect != null) {
			currentVisualEffect.update(m_squares[currentVisualEffect.getIndex()]);

			if (currentVisualEffect.getLifeSpan() == 0) {
				if (visualEffects.size() > 0) {
					currentVisualEffect = visualEffects.poll();
					m_squares[currentVisualEffect.getIndex()].reposition(currentVisualEffect.getPosition());
					m_squares[currentVisualEffect.getIndex()].setColour(currentVisualEffect.getRed(),
							currentVisualEffect.getGreen(), currentVisualEffect.getBlue());
				} else {
					currentVisualEffect = null;
				}
			}
		}

	}

	public void Draw(int matrixloc, int tint, FloatBuffer matrix44fbuffer) {
		if (currentVisualEffect != null) {
			m_squares[currentVisualEffect.getIndex()].Draw(matrixloc, tint, matrix44fbuffer);
		}

	}

	public void addEffect(FX effect) {
		if (currentVisualEffect == null) {
			currentVisualEffect = effect;
			m_squares[currentVisualEffect.getIndex()].reposition(currentVisualEffect.getPosition());
			m_squares[currentVisualEffect.getIndex()].setColour(currentVisualEffect.getRed(),
					currentVisualEffect.getGreen(), currentVisualEffect.getBlue());

		} else {
			visualEffects.add(effect);
		}

	}

	public boolean getActive() {
		if (currentVisualEffect != null) {
			return true;
		}
		return false;
	}

	public void discard() {
		for (int i = 0; i < m_squares.length; i++) {
			m_squares[i].Discard();
		}

	}
}
