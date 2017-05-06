<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="/css/common.css" />
	<link rel="stylesheet" href="/css/searchresult.css" />
	<script src="/js/thirdpartyrequest.js"></script>
	<title>Tiny Search Engine</title>
</head>
<body class="container">
<div class="col-lg-10">
	<div id="search-result-header" class="col-lg-12">
		<div id="search-form-container">
			<form id="search-form" method="post" action="/search">
				<div class="form-group">
					<input id="search-bar" type="text" name="query" value="${(query)!}" data-stemmed-terms="${stemmedTerms}" placeholder="Search here..." required>
					<button class="btn btn-primary" type="submit">Search</button>
				</div>
				<label id="enable-amazon-box">
					<input id="amazon-checkbox" type="checkbox" name="enable-amazon" value="amazon" ${amazonChecked}>Amazon</input>
				</label>
				
				<label id="enable-ebay-box">
					<input id="ebay-checkbox" type="checkbox" name="enable-ebay" value="ebay" ${ebayChecked}>Ebay</input>
				</label>
				
				<label id="enable-youtube-box">
					<input id="youtube-checkbox" type="checkbox" name="enable-youtube" value="youtube" ${youtubeChecked}>Youtube</input>
				</label>	
			</form>
		</div>
	</div>
	
	<div id="thirdparty-result-container" class="col-lg-offset-3 col-lg-6">		

	</div>
	
	<div id="search-result-container" class="col-lg-offset-3 col-lg-6">
		<#list searchResults as resultItem>
			<div class="search-result-item" data-score="${resultItem.score}" data-pg-score="${resultItem.pgRankScore}">
				<h3 class="search-result-item-link">
					<a href="${resultItem.url}">${resultItem.title}</a>
				</h3>
				<div class="search-result-item-body">
					<div><cite>${resultItem.url}</cite></div>
					<span>${resultItem.summary}</span>
				</div>
			</div>
		</#list> 
	</div>
</div>	

<div id="youtube-container" class="col-lg-2">

</div>

</body>
</html>
