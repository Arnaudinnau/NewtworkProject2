function displayText() {
    // Get the value from the input field
    var userInput = document.getElementById('userInput').value;
  
    // Get the div where you want to display the text
    var outputDiv = document.getElementById('output');
  
    // Create a new paragraph element
    var newParagraph = document.createElement('p');
  
    // Set the text of the paragraph to the user input
    newParagraph.textContent = userInput;
  
    // Append the new paragraph to the output div
    outputDiv.appendChild(newParagraph);
  
    // Clear the input field after displaying
    document.getElementById('userInput').value = '';
  }
  