$(function(){
	$("#publishBtn").click(publish);
});

function publish() {

	console.log("you are here");

	$("#publishModal").modal("hide");

	//获取输入的标题和内容
	const title = $("#recipient-name").val();
	const content = $("#message-text").val();

	$.post(
		"/community/discuss/add",
		{"title":title,"content":content},
		function (data){
			data = $.parseJSON(data);
			//显示消息到提示框中
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//两秒后自动隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷星页面
				if(data.code===0){
					window.location.reload();
				}
			}, 2000);
		}
	);
}