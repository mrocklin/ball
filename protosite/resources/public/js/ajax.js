



$( document ).ready( function() {

    oTable = $('#example').dataTable({
        "bProcessing": true,
        "sAjaxSource": "/team/NYN/1990/",
        "iDisplayLength": 25,
		"fnServerData": function ( sUrl, aoData, fnCallback, oSettings ) {
			oSettings.jqXHR = $.ajax( {
				"url":  sUrl,
				"data": aoData,
				"success": function (json) {
					if ( json.sError ) {
						oSettings.oApi._fnLog( oSettings, 0, json.sError );
					}
					
					$(oSettings.oInstance).trigger('xhr', [oSettings, json]);
					fnCallback( json );
				},
				"dataType": "json",
				"cache": true,
				"type": "GET",
				"error": function (xhr, error, thrown) {
					if ( error == "parsererror" ) {
						oSettings.oApi._fnLog( oSettings, 0, "DataTables warning: JSON data from "+
							"server could not be parsed. This is caused by a JSON formatting error." );
					}
				}
			} );
		},
    });
    console.log(oTable);

    function updateDataTable(tbl, team, year){
        var url = "/team/" + team + "/" + year + "/";
        console.log(url);
        tbl.fnReloadAjax(url);
    }

    $("#year").bind('keypress', function(e) {
        var val = $("#year").val() + String.fromCharCode(e.keyCode);
        console.log(val);
        if(val.length == 4){
            console.log("call update");
            updateDataTable(oTable, "NYN", val);
        }
    });
});

