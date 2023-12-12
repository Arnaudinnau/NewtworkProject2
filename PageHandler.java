import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * This class contains the HTML, CSS, and JS code.
 * It provides the entire interface for the client to play and send queries to
 * the server.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */
public class PageHandler {
	private Vector<String> gameState;
	private boolean isEmpty;
	private int nbTries;
	private int nbBoxes;

	/**
	 * Constructor for a specific gameState
	 * 
	 * @param gameState
	 */
	public PageHandler(Vector<String> gameState) {
		this.gameState = gameState;
		int tries = gameState.size();
		this.nbTries = tries;
		this.nbBoxes = 5 * tries;

		if (tries == 0)
			this.isEmpty = true;
		else
			this.isEmpty = false;
	}

	/**
	 * Returns the complete html file in byte[] format
	 * 
	 * @return
	 */
	public byte[] getHTML() {
		String htmlCode = this.stringHTML();
		byte[] byteHTML = htmlCode.getBytes();
		return byteHTML;
	}

	/**
	 * Returns the complete html file in byte[] format
	 * using gzip compression
	 * 
	 * @return
	 */
	public byte[] getCompressedHTML() throws IOException {
		String htmlCode = this.stringHTML();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(htmlCode.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(htmlCode.getBytes(StandardCharsets.UTF_8));
		gzip.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		return compressed;
	}

	public byte[] compress(String data) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data.getBytes(StandardCharsets.UTF_8));
		gzip.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		return compressed;
	}

	/**
	 * Returns the complete html file in String format
	 * 
	 * @return
	 */
	private String stringHTML() {
		String cssCode = stringCSS();
		String jsCode = this.stringJS();
		String htmlCode = "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title>Wordle</title>\n" +
				"<link rel=\"icon\" type=\"image/x-icon\" href=" + stringPNG() + ">\n" +
				"<style>\n" +
				cssCode + "\n" +
				"</style>\n" +
				"</head>\n";

		htmlCode += "<body>\n" +
				"<p style=\"text-align:center;\"> <img src=" + stringPNG()
				+ " width=\"404\" height=\"102\"></p>\n";

		// DISPLAYING WORDS
		int boxes;
		int tries;

		// new game
		if (this.isEmpty) {
			for (tries = 0; tries < 6; tries++) {
				htmlCode += "<div class=\"input-boxes\">\n";
				for (boxes = 0; boxes < 5; boxes++) {
					htmlCode += "<div class=\"input-box\" ></div>\n";
				}
				htmlCode += "</div>\n";
			}
		}

		// already began game
		else {
			// already sent tries
			for (tries = 0; tries < nbTries; tries++) {
				htmlCode += "<div class=\"input-boxes\">\n";
				for (boxes = 0; boxes < 5; boxes++) {
					String color = Character.toString(gameState.get(tries).charAt(boxes + 5));
					if (color.equals("G")) {
						color = "#1cdf21";
					} else if (color.equals("Y")) {
						color = "#f4d03f";
					} else {
						color = "#515a5a";
					}

					htmlCode += "<div class=\"input-box\" style=\"background-color:" + color + ";\">"
							+ Character.toString(gameState.get(tries).charAt(boxes))
							+ "</div>\n";
				}
				htmlCode += "</div>\n";
			}

			// available tries
			for (tries = 0; tries < 6 - nbTries; tries++) {
				htmlCode += "<div class=\"input-boxes\">\n";
				for (boxes = 0; boxes < 5; boxes++) {
					htmlCode += "<div class=\"input-box\" ></div>\n";
				}
				htmlCode += "</div>\n";
			}

		}

		// JS AUTHORIZED
		htmlCode += "<div id=\"js-abled\">\n";

		// displaying only if NONEXISTENT or WRONG guess
		htmlCode += "<div class=\"wrongTry\" id=\"wrongTry\"></div>\n";

		// displaying when game is over
		htmlCode += "<div class=\"endGame\" id=\"endGame\"></div>\n";

		// keyboard
		htmlCode += "<div class=\"keyboard\">\n" +
				"<button id = letter onclick=\"appendToInput('A')\">A</button>\n" +
				"<button id = letter onclick=\"appendToInput('Z')\">Z</button>\n" +
				"<button id = letter onclick=\"appendToInput('E')\">E</button>\n" +
				"<button id = letter onclick=\"appendToInput('R')\">R</button>\n" +
				"<button id = letter onclick=\"appendToInput('T')\">T</button>\n" +
				"<button id = letter onclick=\"appendToInput('Y')\">Y</button>\n" +
				"<button id = letter onclick=\"appendToInput('U')\">U</button>\n" +
				"<button id = letter onclick=\"appendToInput('I')\">I</button>\n" +
				"<button id = letter onclick=\"appendToInput('O')\">O</button>\n" +
				"<button id = letter onclick=\"appendToInput('P')\">P</button>\n" +
				"<button id = letter onclick=\"appendToInput('Q')\">Q</button>\n" +
				"<button id = letter onclick=\"appendToInput('S')\">S</button>\n" +
				"<button id = letter onclick=\"appendToInput('D')\">D</button>\n" +
				"<button id = letter onclick=\"appendToInput('F')\">F</button>\n" +
				"<button id = letter onclick=\"appendToInput('G')\">G</button>\n" +
				"<button id = letter onclick=\"appendToInput('H')\">H</button>\n" +
				"<button id = letter onclick=\"appendToInput('J')\">J</button>\n" +
				"<button id = letter onclick=\"appendToInput('K')\">K</button>\n" +
				"<button id = letter onclick=\"appendToInput('L')\">L</button>\n" +
				"<button id = letter onclick=\"appendToInput('M')\">M</button>\n" +
				"<button class=\"cheatButton\" onmouseover=\"cheatResponse(this)\" onmouseout=\"resetText(this)\">Cheating</button>\n"
				+
				"<button id = letter onclick=\"tryResponse()\"> &crarr; </button>\n" +
				"<button id = letter onclick=\"appendToInput('W')\">W</button>\n" +
				"<button id = letter onclick=\"appendToInput('X')\">X</button>\n" +
				"<button id = letter onclick=\"appendToInput('C')\">C</button>\n" +
				"<button id = letter onclick=\"appendToInput('V')\">V</button>\n" +
				"<button id = letter onclick=\"appendToInput('B')\">B</button>\n" +
				"<button id = letter onclick=\"appendToInput('N')\">N</button>\n" +
				"<button id = letter onclick=\"deleteLastCharacter()\">&lArr;</button>\n" +
				"</div>\n";
		htmlCode += "<script>\n" +
				jsCode + " \n" +
				"</script>\n" +
				"</div>\n";

		// JS DISABLED
		htmlCode += "<div id=\"js-disabled\">\n" +
				"<noscript>\n";

		// form submission possible only when game is not over
		if (isEmpty || !(gameState.get(nbTries - 1).contains("GAMEOVER"))) {
			htmlCode += "<form method=\"post\" class=\"form\" enctype=\"text/plain\">\n" +
					"<label for=\"TRY\">Your guess :</label>\n" +
					"<input type=\"text\" name=\"TRY\" id=\"TRY\" maxlength=\"5\" style=\"text-transform:uppercase\">\n"
					+
					"<input type=\"submit\" value=\"Submit\">\n" +
					"</form>\n";
		}
		// displaying when game is over
		else {
			if (gameState.get(nbTries - 1).contains("GGGGG")) {
				htmlCode += "<div class=\"endGame\" id=\"endGame\" style=\"background-color: #1cdf21; display : block;\">You win !</div>\n";
			} else {
				htmlCode += "<div class=\"endGame\" id=\"endGame\" style=\"background-color: darkred; display : block;\">You lose ! </div>\n";
			}
		}

		htmlCode += "</noscript>\n" +
				"</div>\n" +
				"</body>\n" +
				"</html>\n";

		return htmlCode;
	}

	/**
	 * Returns the JS file in String format
	 * 
	 * @return
	 */
	private String stringJS() {

		String jsCode = "let currentBox = " + nbBoxes + ";\n" +

				"let currentTry = " + nbTries + ";\n" +

				"const inputBoxes = document.querySelectorAll('.input-box');\n" +

				"document.addEventListener('DOMContentLoaded', showDisplay);\n" +

				// showing the correct interface dependanding on whether JS is authorized
				"function showDisplay(){\n" +
				"document.getElementById('js-abled').style.display = 'block';\n" +
				"document.getElementById('js-disabled').style.display = 'none';\n" +
				"}\n" +

				// filling the guesses with own keyboard
				"event = document.addEventListener('keydown', keyboardInput);\n" +

				"function keyboardInput(event){\n" +
				"const start = currentTry*5;\n" +
				"const range = /[A-Za-z]/;\n" +
				"const validInput = range.test(event.key);\n" +
				"if (event.key.length === 1 && currentBox < start+5 && validInput){\n" +
				"inputBoxes[currentBox].innerText = event.key.toUpperCase();\n" +
				"currentBox++;\n" +
				"}\n" +
				"else if (event.key === 'Enter'){\n" +
				"tryResponse();\n" +
				"}\n" +
				"else if (event.key === 'Backspace'){\n" +
				"deleteLastCharacter();\n" +
				"}\n" +
				"}\n" +

				// filling the guesses with screen keyboard
				"function appendToInput(value){\n" +
				"const start = currentTry*5;\n" +
				"if(currentBox < start+5){\n" +
				"inputBoxes[currentBox].innerText = value;\n" +
				"currentBox++;\n" +
				"}\n" +
				"}\n" +

				// deleting last character
				"function deleteLastCharacter(){\n" +
				"const start = currentTry*5\n" +
				"if(currentBox > start){\n" +
				"currentBox--;\n" +
				"inputBoxes[currentBox].innerText = '';\n" +
				"}\n" +
				"}\n" +

				// sending an AJAX request and displaying the answer (TRY)
				"function tryResponse(){\n" +
				"const start = currentTry*5;\n" +
				"let input ='';\n" +

				"for(let i = start; i<start+5; i++){\n" +
				"input += inputBoxes[i].innerText;\n" +
				"}\n" +

				"let output;\n" +
				"let xhttp = new XMLHttpRequest();\n" +

				"xhttp.onreadystatechange = function(){\n" +
				"if(xhttp.status == 200 && xhttp.readyState == 4){\n" +
				"output = xhttp.responseText;\n" +

				"if(output == 'WRONG'){\n" +
				"let wrongTry = document.getElementById('wrongTry');\n" +
				"wrongTry.innerText = 'Guess needs to be 5 letters long';\n" +

				"wrongTry.style.display = 'block';\n" +
				"setTimeout(function(){\n" +
				"wrongTry.style.display='none';\n" +
				"}, 2000);\n" +
				"return;\n" +
				"}\n" +

				"if(output == 'NONEXISTENT'){\n" +
				"let wrongTry = document.getElementById('wrongTry');\n" +
				"wrongTry.innerText = \"This word doesn't exist\";\n" +

				"wrongTry.style.display = 'block';\n" +
				"setTimeout(function(){\n" +
				"wrongTry.style.display='none';\n" +
				"}, 2000);\n" +
				"return;\n" +
				"}\n" +

				"let color = 0;\n" +
				"for(let i = start; i<start+5; i++){\n" +
				"if(output.charAt(color) == 'G')\n" +
				"inputBoxes[i].style.backgroundColor = '#1cdf21';\n" +

				"else if (output.charAt(color) == 'Y')\n" +
				"inputBoxes[i].style.backgroundColor = '#f4d03f';\n" +

				"else\n" +
				"inputBoxes[i].style.backgroundColor = '#515a5a';\n" +

				"color++\n" +
				"}\n" +
				"currentTry++;\n" +

				"if(output.includes(\"GAMEOVER\")){\n" +
				"let cheat;\n" +
				"xhttp2 = new XMLHttpRequest();\n" +
				"xhttp2.onreadystatechange = function(){\n" +
				"if(xhttp2.status==200 && xhttp2.readyState == 4){\n" +
				"cheat = xhttp2.responseText;\n" +
				"let endGame = document.getElementById('endGame');\n" +
				"if(output.includes(\"GGGGG\")){\n" +
				"endGame.innerText=\"YOU WIN, the word was : \"+ cheat;\n" +
				"endGame.style.backgroundColor='#1cdf21';\n" +
				"}\n" +
				"else{\n" +
				"endGame.innerText=\"GAMEOVER, the word was : \"+ cheat;\n" +
				"endGame.style.backgroundColor='#df301c';\n" +
				"}\n" +
				"endGame.style.display='block';\n" +
				"}\n" +
				"};\n" +
				"xhttp2.open(\"GET\", \"/play.html?CHEAT\");\n" +
				"xhttp2.send();\n" +

				"currentTry=5;\n" +
				"currentBox=30;\n" +
				"}\n" +

				"}\n" +
				"};\n" +
				"xhttp.open(\"GET\", \"/play.html?TRY=\" + input);\n" +
				"xhttp.send();\n" +
				"}\n" +

				// sending a AJAX request and displaying the answer (CHEAT)
				"function cheatResponse(cheatButton){\n" +
				"let xhttp = new XMLHttpRequest();\n" +
				"let output;\n" +
				"xhttp.onreadystatechange = function(){\n" +
				"if(xhttp.status == 200 && xhttp.readyState == 4){\n" +
				"output = xhttp.responseText;\n" +
				"cheatButton.innerText = output;\n" +
				"}\n" +
				"};\n" +
				"xhttp.open(\"GET\", \"/play.html?CHEAT\");\n" +
				"xhttp.send();\n" +
				"}\n" +

				// Secret word has to disappear if button 'Cheating' isn't hovered
				"function resetText(cheatButton){\n" +
				"cheatButton.innerText = 'Cheating';\n" +
				"}\n";

		return jsCode;

	}

	/**
	 * Returns the CSS file in String format
	 * 
	 * @return
	 */
	private static String stringCSS() {
		String cssCode = "body {background-color: #1D2067  ;}" +

				"#js-abled{\n" +
				"display: none;\n" +
				"}\n" +

				"#js-disabled{\n" +
				"display: block;\n" +
				"}\n" +

				".keyboard {\n" +
				"margin-top: 40px;\n" +
				"margin-left: 40px;\n" +
				"margin-right: 40px;\n" +
				"display: grid;\n" +
				"grid-template-columns: repeat(10, 1fr);\n" +
				"gap: 5px;\n" +
				"}\n" +

				"#letter{\n" +
				"padding: 10px;\n" +
				"font-size: 16px;\n" +
				"background-color: #498ADE ;\n" +
				"color: white;\n" +
				"border: 1px solid black;\n" +
				"outline: none;\n" +
				"border-radius: 5px;\n" +
				"cursor: pointer;\n" +
				"}\n" +

				".input-boxes {\n" +
				"display: flex;\n" +
				"justify-content: center;\n" +
				"align-items: center;\n" +
				"height: 40px;\n" +
				"margin-bottom: 10px;\n" +
				"}\n" +

				".input-box {\n" +
				"border: 1px solid black;\n" +
				"outline : none;\n" +
				"width: 40px;\n" +
				"height: 40px;\n" +
				"line-height: 40px;\n" +
				"margin-right: 10px;\n" +
				"display: flex;\n" +
				"justify-content: center;\n" +
				"align-items: center;\n" +
				"font-size: 18px;\n" +
				"background-color: white;\n" +
				"}\n" +

				".cheatButton{\n" +
				"padding: 10px;\n" +
				"font-size: 16px;\n" +
				"background-color: white;\n" +
				"border: 4px solid darkred;\n" +
				"color:  darkred;\n" +
				"cursor: pointer;\n" +
				"border-radius: 30px;\n" +
				"}\n" +

				".cheatButton:hover{\n" +
				"background-color: darkred;\n" +
				"color : white;\n" +
				"}\n" +

				".wrongTry{\n" +
				"display: none;\n" +
				"position: fixed;\n" +
				"top: 30%;\n" +
				"left: 10%;\n" +
				"right: 70%;\n" +
				"background-color: black;\n" +
				"color: white;\n" +
				"padding: 10px;\n" +
				"}\n" +

				".endGame{\n" +
				"display: none;\n" +
				"position: fixed;\n" +
				"top: 50%;\n" +
				"left: 30%;\n" +
				"right: 30%;\n" +
				"color: white;\n" +
				"border-radius: 30px;\n" +
				"font-size: 40px;\n" +
				"padding: 30px;\n" +
				"text-align: center;\n" +
				"}\n" +

				"form.form{\n" +
				"display :table;\n" +
				"margin-left: auto;\n" +
				"margin-right: auto;\n" +
				"margin-top : 40px;\n" +
				"}\n" +

				"label,\n" +
				"input{\n" +
				"display: table-cell;\n" +
				"margin-bottom: 10px;\n" +
				"margin-right: 20px;\n" +
				"}\n" +

				"label{\n" +
				"padding-right: 10px;\n" +
				"color : white;\n" +
				"}\n";

		return cssCode;

	}

	/**
	 * Returns the PNG file in String format using base64 encoding
	 * 
	 * @return
	 */
	private static String stringPNG() {
		try {
			File file = new File("logo.png");
			BufferedImage image = ImageIO.read(file);
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "png", os);
			String s = Base64.getEncoder().encodeToString(os.toByteArray());
			String htmlImage = "data:image/png;base64," + s;
			return htmlImage;
		} catch (IOException e) {
			System.out.println(e);
		}

		return null;
	}
}
