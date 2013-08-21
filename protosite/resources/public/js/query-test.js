
$( document ).ready( function() {
    $.ajax({
        url: "/querys/",
        dataType: "json",
        data: JSON.stringify({want: ["yearID", "AB"], constraints: [["playerID", "strawda01"]]}),
        success: function(data, aStatus, dummy){
            console.log(data);
        }
    });


});
