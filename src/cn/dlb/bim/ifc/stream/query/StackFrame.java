package cn.dlb.bim.ifc.stream.query;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import cn.dlb.bim.database.DatabaseException;

public abstract class StackFrame {
	private boolean done = false;
	
	public boolean isDone() {
		return done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public abstract boolean process() throws DatabaseException, QueryException, JsonParseException, JsonMappingException, IOException;
}