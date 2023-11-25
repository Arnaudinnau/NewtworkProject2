function displayText() {
    // Get the value from the input field
    var userInput = document.getElementById('userInput').value;
  
    var output = 'BBBBB'
  
    document.getElementById('userInput').value = output;
  }
  
  function appendToInput(value){
    const input = document.getElementById('userInput');
    if(value == 'delete'){
      input.value='';
    }
    else{
      input.value += value;
    }
  }