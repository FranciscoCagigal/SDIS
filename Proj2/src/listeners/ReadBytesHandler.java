package listeners;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadBytesHandler {

	private BufferedReader input;
	private String body;
	private int inputSize;
	
	public ReadBytesHandler(BufferedReader input) throws IOException {
		
		this.input = input;
		
		body = input.readLine();
		
		inputSize = body.length();
	}

	/**
	 * @return the inputSize
	 */
	public int getInputSize() {
		return inputSize;
	}

	/**
	 * @param inputSize the inputSize to set
	 */
	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
