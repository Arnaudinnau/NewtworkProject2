import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PageHandler {
	private Vector<String> gameState; // forme des string du vecteur : "WORLDCOLOR" (collés sans rien entre)
	private boolean isEmpty;
	private int nbTries;
	private int nbBoxes;

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

	private String stringHTML() {
		String cssCode = this.stringCSS();
		String jsCode = this.stringJS();
		String htmlCode = "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"<title>Wordle</title>\n" +
				"<link rel=\"icon\" type=\"image/x-icon\" href=\"icon.png\">\n" +
				"<style>\n" +
				cssCode + "\n" +
				"</style>\n" +
				"</head>\n";

		htmlCode += "<body>\n" +
				"<p style=\"text-align:center;\"> <img src=\"logo.png\" width=\"404\" height=\"102\"></p>\n" +
				"<div id=\"js-abled\">\n";

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
					if (color.equals('G')) {
						color = "#1cdf21";
					} else if (color.equals('Y')) {
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
			for (tries = 0; tries < 5 - nbTries; tries++) {
				htmlCode += "<div class=\"input-boxes\">\n";
				for (boxes = 0; boxes < 5; boxes++) {
					htmlCode += "<div class=\"input-box\" ></div>\n";
				}
				htmlCode += "</div>\n";
			}

		}

		htmlCode += "<div class=\"wrongTry\" id=\"wrongTry\"></div>\n";

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

		htmlCode += "<div id=\"js-disabled\">\n" +
				"<noscript>\n" +
				"<form action=\"/play.html?POST\" method=\"post\" class=\"form\">\n";

		// new game
		if (this.isEmpty) {
			for (tries = 1; tries < 7; tries++) {
				htmlCode += "<div class=\"form\">\n" +
						"<label for=\"try" + tries + "\">Try " + tries + " :</label>\n" +
						"<input type=\"text\" id=\"try" +
						"boxes \"required maxlength=\"5\">\n" +
						"<input type=\"submit\" value=\"Submit\">\n" +
						"</div>\n";
			}
		}

		// already began game
		else {
			// already sent tries
			for (tries = 0; tries < nbTries; tries++) {
			}

			// available tries
			for (tries = nbTries + 1; tries <= 5; tries++) {
				htmlCode += "<div class=\"form\">\n" +
						"<label for=\"try" + tries + "\">Try " + tries + " :</label>\n" +
						"<input type=\"text\" id=\"try" +
						"boxes \"required maxlength=\"5\">\n" +
						"<input type=\"submit\" value=\"Submit\">\n" +
						"</div>\n";
			}
		}

		htmlCode += "</form>\n" +
				"</noscript>\n" +
				"</div>\n" +
				"</body>\n" +
				"</html>\n";

		return htmlCode;
	}

	private String stringJS() {
		// afficher les couleurs déjà données ici?

		String jsCode = "let currentBox = " + nbBoxes + ";\n" +

				"let currentTry = " + nbTries + ";\n" +

				"const inputBoxes = document.querySelectorAll('.input-box');\n" +

				"document.addEventListener('DOMContentLoaded', showDisplay);\n" +

				"function showDisplay(){\n" +
				"document.getElementById('js-abled').style.display = 'block';\n" +
				"document.getElementById('js-disabled').style.display = 'none';\n" +
				"}\n" +

				"event = document.addEventListener('keydown', keyboardInput);\n" +

				"function keyboardInput(event){\n" +
				"const start = currentTry*5\n" +
				"if (event.key.length === 1 && currentBox < start+5 ){\n" +
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

				"function appendToInput(value){\n" +
				"const start = currentTry*5;\n" +
				"if(currentBox < start+5){\n" +
				"inputBoxes[currentBox].innerText = value;\n" +
				"currentBox++;\n" +
				"}\n" +
				"}\n" +

				"function deleteLastCharacter(){\n" +
				"const start = currentTry*5\n" +
				"if(currentBox > start){\n" +
				"currentBox--;\n" +
				"inputBoxes[currentBox].innerText = '';\n" +
				"}\n" +
				"}\n" +

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
				"}\n" +
				"};\n" +
				"xhttp.open(\"GET\", \"/play.html?TRY=\" + input);\n" +
				"xhttp.send();\n" +
				"}\n" +

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

				"function resetText(cheatButton){\n" +
				"cheatButton.innerText = 'Cheating';\n" +
				"}\n";

		return jsCode;

	}

	private String stringCSS() {
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

				"form.form{\n" +
				"display :table;\n" +
				"margin-left: auto;\n" +
				"margin-right: auto;\n" +
				"margin-top : 40px;\n" +
				"}\n" +

				"div.form{\n" +
				"display: table-row;\n" +
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

	public byte[] getHTML() {
		String htmlCode = this.stringHTML();
		byte[] byteHTML = htmlCode.getBytes();
		return byteHTML;
	}

	public byte[] getJS() {
		String jsCode = this.stringJS();
		byte[] byteJS = jsCode.getBytes();
		return byteJS;
	}

	public byte[] getCSS() {
		String cssCode = this.stringCSS();
		byte[] byteCSS = cssCode.getBytes();
		return byteCSS;
	}

	public byte[] getPNG() throws IOException {
		File file = new File("logo.png");
		BufferedImage image = ImageIO.read(file);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "png", os);
		String s = Base64.getEncoder().encodeToString(os.toByteArray());
		String htmlImage = "data:image/png;base64," + s;
		return htmlImage.getBytes();
	}
}
