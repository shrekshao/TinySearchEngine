<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="/css/common.css" />
	<link rel="stylesheet" href="/css/index.css" />
	<title>Tiny Search Engine</title>
</head>
<body class="container">
	<div id="index-container" class="col-lg-offset-3 col-lg-6">
		<h1 id="title" class="lead">TinySearchEngine</h1>
		<div id="search-form-container">
			<form id="search-form" method="post" action="/search">
				<div class="form-group">
					<input type="text" name="query" placeholder="Search here..." required>
					<button class="btn btn-primary" type="submit">Search</button>
				</div>
				<label id="enable-amazon-box">
					<input type="checkbox" name="enable-amazon" value="amazon">Amazon</input>
				</label>
				<label id="enable-ebay-box">
					<input type="checkbox" name="enable-ebay" value="ebay">Ebay</input>
				</label>
				<label id="enable-youtube-box">
					<input type="checkbox" name="enable-youtube" value="youtube">Youtube</input>
				</label>
				<label id="enable-spellcheck-box">
					<input type="checkbox" name="enable-spellcheck" value="spellcheck">Spell Check</input>
				</label>	
			</form>
		</div>
	</div>
</body>
</html>
