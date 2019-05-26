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
				closeAfterAdd: true,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					delete data.oper;
					return JSON.stringify(data);
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'channel/' + postdata.id;
		}	
	};
	var addOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'channel';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'channel/' + postdata;
		}
	};

	var URL = '/allChannels';
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
				editoptions: {disabled: true, size:5}
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
			}
		],
		caption: "Channels",
		pager : '#pagerChannels',
		height: 'auto',
		forceFit: true,
        autowidth: true,
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#channels")
			.jqGrid(options)
			.navGrid('#pagerChannels',
			{addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#channels").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });
	
});
