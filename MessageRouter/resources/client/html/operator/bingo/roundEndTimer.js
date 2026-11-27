var timer=setInterval(onTimer,1000);
function onTimer()
{
	 end--;
	 if (end<0) {
		clearInterval(timer); 
		location="/bingo/results"+token;
	 } else {
		 document.getElementById("timer").innerHTML="Round ends in "+end+" seconds";
		 
	 }
	
}