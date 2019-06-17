$(function() {

	$.extend($.jgrid.defaults, {
				datatype: 'json',
				jsonReader : {
					repeatitems:false,
					total: function(result) {
						//Total number of pages
						return Math.ceil(result.total / result.max);
					},
					records: function(result) {
						//Total number of records
						return result.total;
					}
				},
				prmNames: {
					page: "page",
					rows: "size",
					sort: "sort"
				},
				sortname: 'name',
				sortorder: 'asc',
				forceFit: true,
		        autowidth: true,
				height: 'auto',
				viewrecords: true,
				rowList: [10, 20, 50, 100],
				altRows: true,
				loadError: function(xhr, status, error) {
					alert(error);
				}
			});

	$.extend($.jgrid.edit, {
				closeAfterEdit: true,
				closeAfterAdd: false,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
					//encodeURIComponent("id") + '=' + encodeURIComponent(data.networkChannel_id);
					//delete data.oper;
					//return JSON.stringify(data);
				},
				errorTextFormat: function (response) {
					if(response.responseText) {
						var obj = JSON.parse(response.responseText);
						if(obj.errorCode && obj.errorCode != null) {
							return obj.errorCode+" "+ obj.errorCause;
						}
					}
				    return "Data Saved!";
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'pack/' + postdata.id;
		},
		afterShowForm: manageChannelGST
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'pack';
		},
		afterShowForm: manageChannelGST,
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'pack/' + postdata;
		}
	};

	var networksData;
	var URL = '/allPacks';
	var options = {
		url: URL,
		editurl: URL,
		height: 'auto',
        forceFit: true,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 10,
		colModel:[
			{
				name:'id',
				label: 'ID',
				index: 'id',
				formatter:'integer',
				editable: true,
				hidden: true, 
				editrules: { edithidden: false }
			},
			{
				name:'name',
				label: 'Name',
				index: 'name',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'price',
				label: 'Price',
				index: 'price',
				editable: true,
				formatter:'number',
				editrules: {required: true}
			},
			{
				name:'gst',
				label: 'GST (18%)',
				index: 'gst',
				editable: true,
				formatter:'number',
				editrules: {required: true}
			},
			{
				name:'total',
				label: 'Total',
				index: 'total',
				editable: true,
				formatter:'number',
				editrules: {required: true}
			}
		],
		caption: "Packs",
		pager : '#pagerPacks',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		},
		multiselect: false
	};

	$("#packs")
			.jqGrid(options)
			.navGrid('#pagerPacks',
			{addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#packs").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

	function manageChannelGST() {
		setTimeout(function() { 
			$("#gst").attr("disabled","disabled");
			$("#gst").attr("readonly","readonly");
			
			$("#total").attr("disabled","disabled");
			$("#total").attr("readonly","readonly");
			
			$("#price").bind("change paste keyup", function (e) {
				var monthlyRent = parseFloat($(this).val());
				var gstValue = parseFloat(monthlyRent * 18 / 100);
				$("#gst").val(gstValue);
				$("#total").val(parseFloat(monthlyRent + gstValue));
			});
			
		}, 100);
    }
	
});
