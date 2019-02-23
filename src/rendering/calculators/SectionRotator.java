package rendering.calculators;

import shared.Vertex;

public class SectionRotator {

	public static Vertex[] generateBlock(int orientation, int offset, Vertex[] v, int degrees) {

		float radians = 0.0174533F * degrees;

		v[offset + 2] = new Vertex(-0.5F, 1.1F, 0, 1.0F, 1.0F);
		v[offset + 3] = new Vertex(-0.5F, 1.2F, 0, 1.0F, 0.0F);

		if (orientation == -1) {
			v[offset + 0] = new Vertex(0.0F, 1.2F, 0, 0.0F, 0.0F);
			v[offset + 1] = new Vertex(0.0F, 1.1F, 0, 1.0F, 0.0F);
		} else {
			v[offset + 0] = new Vertex(0.5F, 1.2F, 0, 0.0F, 0.0F);
			v[offset + 1] = new Vertex(0.5F, 1.1F, 0, 1.0F, 0.0F);
		}

		if (orientation == 1) {
			v[offset + 2] = new Vertex(-0.0F, 1.1F, 0, 1.0F, 1.0F);
			v[offset + 3] = new Vertex(-0.0F, 1.2F, 0, 1.0F, 0.0F);
		} else {
			v[offset + 2] = new Vertex(-0.5F, 1.1F, 0, 1.0F, 1.0F);
			v[offset + 3] = new Vertex(-0.5F, 1.2F, 0, 1.0F, 0.0F);
		}

		for (int i = 0; i < 4; i++) {
			double cos = Math.cos(radians);
			double sin = Math.sin(radians);

			float rx = (float) ((cos * v[offset + i].pos[0]) - (sin * v[offset + i].pos[1]));
			float ry = (float) ((sin * v[offset + i].pos[0]) + (cos * v[offset + i].pos[1]));

			v[offset + i].pos[0] = rx;
			v[offset + i].pos[1] = ry;
		}
		return v;
	}

}
