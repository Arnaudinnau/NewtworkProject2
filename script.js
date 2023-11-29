function displayText() {
  // Get the value from the input field
  var userInput = document.getElementById('userInput').value;

  var output;

  var xhttp = new XMLHttpRequest();

  xhttp.onreadystatechange = function(){
    if(xhttp.status == 200 && xhttp.readyState == 4){
      console.log('on est ici');
      document.getElementById("userInput").value = xhttp.responseText;
      console.log(xhttp.responseText);
    }
  };
  xhttp.open("GET", "/test.html?TRY="+userInput);
  xhttp.send()
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