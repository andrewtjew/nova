var timer=setInterval(onTimer,100);
var index=0;
var alt=0;
function onTimer()
{
	if (index<result.ballResults.length) {
		var ballResult=result.ballResults[index];
		if (ballResult.id!=undefined) {
			$("#"+ballResult.id).attr("style","padding:10px;width:40px;background-color:#CCC;");
		}
		$("#ballsTable").append("<td>"+ballResult.number+"</td>");
		index++;
		if (index==result.ballResults.length) {
			$("#playAgain").removeAttr("disabled");
			$("#card").removeAttr("disabled");
			$("#select").removeAttr("disabled");
			
		}
			
	} else {
		var winIds=result.winIds;
		if (winIds.length>0) {
			for (i=0;i<winIds.length;i++) {
				if (alt%8<4) {
					$("#"+winIds[i]).attr("style","padding:10px;width:40px;background-color:#8F8;");
				} else {
					$("#"+winIds[i]).attr("style","padding:10px;width:40px;background-color:#CCC;");
					
				} 
			}
			if (alt%8<3) {
				$("#result").html("&nbsp;");
			} else {
				$("#result").html("Winner");
			}
			alt++;
		}
		else {
			$("#result").html("Better luck next time");
		}
			
	}
	
		
}