package cn.dlb.bim.vo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.google.gson.stream.JsonWriter;

import cn.dlb.bim.ifc.engine.cells.Colord;
import cn.dlb.bim.models.geometry.GeometryInfo;

public class GeometryInfoVo {

	private Long oid;
	private int[] indices;
	private float[] vertices;
	private float[] normals;
	private int[] indicesForLinesWireFrame;
	private Bound bound;
	private String typeName;
	private Boolean defaultVisiable;
	private Colord color;
	
	public GeometryInfoVo() {
		init();
	}
	
	public void writeJson(JsonWriter jsonWriter) throws IOException {
		jsonWriter.beginObject();
		
		jsonWriter.name("oid").value(oid);
		
		jsonWriter.name("indices");
		jsonWriter.beginArray();
		for (int i : indices) {
			jsonWriter.beginObject();
			jsonWriter.name("i").value(i);
			jsonWriter.endObject();
		}
		jsonWriter.endArray();
		jsonWriter.name("vertices");
		jsonWriter.beginArray();
		for (float v : vertices) {
			jsonWriter.beginObject();
			jsonWriter.name("v").value(v);
			jsonWriter.endObject();
		}
		jsonWriter.endArray();
		jsonWriter.name("normals");
		jsonWriter.beginArray();
		for (float n : normals) {
			jsonWriter.beginObject();
			jsonWriter.name("n").value(n);
			jsonWriter.endObject();
		}
		jsonWriter.endArray();
		
		jsonWriter.name("bound");
		bound.writeJson(jsonWriter);
		
		jsonWriter.name("typeName").value(typeName);
		jsonWriter.name("defaultVisiable").value(defaultVisiable);
		
		if (color != null) {
			jsonWriter.name("color");
			color.writeJson(jsonWriter);
		}
		
		jsonWriter.endObject();
	}
	
	public void init() {
		bound = new Bound();
	}
	
	public void transform(GeometryInfo geometryInfo, Long oid, String typeName, Boolean defaultVisiable, Colord color) {
		this.oid = oid;
		this.color = color;
		indices = byteArrayToIntArray(geometryInfo.getData().getIndices());
		vertices = byteArrayToFloatArray(geometryInfo.getData().getVertices());
		normals = byteArrayToFloatArray(geometryInfo.getData().getNormals());
		indicesForLinesWireFrame = byteArrayToIntArray(geometryInfo.getData().getIndicesForLinesWireFrame());
		double maxX = geometryInfo.getMaxBounds().getX();
		double maxY = geometryInfo.getMaxBounds().getY();
		double maxZ = geometryInfo.getMaxBounds().getZ();
		double minX = geometryInfo.getMinBounds().getX();
		double minY = geometryInfo.getMinBounds().getY();
		double minZ = geometryInfo.getMinBounds().getZ();
		bound.max.set(maxX, maxY, maxZ);
		bound.min.set(minX, minY, minZ);
		this.typeName = typeName;
		this.defaultVisiable = defaultVisiable;
	}
	
	private int[] byteArrayToIntArray(byte[] byteArray) {
		 IntBuffer intBuf =
				   ByteBuffer.wrap(byteArray)
				     .order(ByteOrder.LITTLE_ENDIAN)
				     .asIntBuffer();
				 int[] array = new int[intBuf.remaining()];
				 intBuf.get(array);
		return array;
	}
	
	private float[] byteArrayToFloatArray(byte[] byteArray) {
		 FloatBuffer floatBuf =
				   ByteBuffer.wrap(byteArray)
				     .order(ByteOrder.LITTLE_ENDIAN)
				     .asFloatBuffer();
				 float[] array = new float[floatBuf.remaining()];
				 floatBuf.get(array);
		return array;
	}

	
	public Long getOid() {
		return oid;
	}

	public void setOid(Long oid) {
		this.oid = oid;
	}

	public int[] getIndices() {
		return indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public float[] getVertices() {
		return vertices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public float[] getNormals() {
		return normals;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public Bound getBound() {
		return bound;
	}

	public void setBound(Bound bound) {
		this.bound = bound;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Boolean getDefaultVisiable() {
		return defaultVisiable;
	}

	public void setDefaultVisiable(Boolean defaultVisiable) {
		this.defaultVisiable = defaultVisiable;
	}

	public Colord getColor() {
		return color;
	}

	public void setColor(Colord color) {
		this.color = color;
	}

	public int[] getIndicesForLinesWireFrame() {
		return indicesForLinesWireFrame;
	}

	public void setIndicesForLinesWireFrame(int[] indicesForLinesWireFrame) {
		this.indicesForLinesWireFrame = indicesForLinesWireFrame;
	}
}
