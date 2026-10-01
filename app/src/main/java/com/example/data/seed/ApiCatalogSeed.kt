package com.example.data.seed

import com.example.model.ApiCategory
import com.example.model.AuthType
import com.example.model.CorsType
import com.example.model.DifficultyLevel
import com.example.model.PublicApi
import com.example.model.DailyProjectChallenge

object ApiCatalogSeed {

    val apis: List<PublicApi> = listOf(
        // WEATHER (5)
        PublicApi(
            id = "open-meteo",
            name = "Open-Meteo",
            description = "Free weather forecast API for non-commercial & commercial use without an API key. 7-day hourly forecasts.",
            category = ApiCategory.WEATHER,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://open-meteo.com/en/docs",
            testEndpoint = "https://api.open-meteo.com/v1/forecast?latitude=37.77&longitude=-122.41&current_weather=true",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("weather", "forecast", "climate", "temperature", "no-auth"),
            sampleJs = """// JavaScript / Fetch
const res = await fetch("https://api.open-meteo.com/v1/forecast?latitude=37.77&longitude=-122.41&current_weather=true");
const data = await res.json();
console.log(`Current Temp: ${'$'}{data.current_weather.temperature}°C`);""",
            samplePy = """# Python / requests
import requests
res = requests.get("https://api.open-meteo.com/v1/forecast?latitude=37.77&longitude=-122.41&current_weather=true")
data = res.json()
print("Current Temp:", data["current_weather"]["temperature"])""",
            sampleDart = """// Dart / http
import 'package:http/http.dart' as http;
import 'dart:convert';

final url = Uri.parse('https://api.open-meteo.com/v1/forecast?latitude=37.77&longitude=-122.41&current_weather=true');
final res = await http.get(url);
final data = jsonDecode(res.body);
print('Temp: ${'$'}{data["current_weather"]["temperature"]}');""",
            scaffoldSummary = "Zero-config weather service. Provides global temperature, wind speed, solar radiation, and precipitation."
        ),
        PublicApi(
            id = "wttr-in",
            name = "wttr.in",
            description = "Terminal-oriented & JSON weather forecast service providing one-line summaries and detailed conditions.",
            category = ApiCategory.WEATHER,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://github.com/chubin/wttr.in",
            testEndpoint = "https://wttr.in/London?format=j1",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("weather", "terminal", "plain-text", "json"),
            sampleJs = """const res = await fetch("https://wttr.in/London?format=j1");
const data = await res.json();
console.log(data.current_condition[0].temp_C + "°C");""",
            samplePy = """import requests
res = requests.get("https://wttr.in/London?format=j1")
print(res.json()["current_condition"][0]["temp_C"])""",
            sampleDart = """import 'package:http/http.dart' as http;
final res = await http.get(Uri.parse('https://wttr.in/London?format=j1'));
print(res.body);"""
        ),
        PublicApi(
            id = "weatherapi",
            name = "WeatherAPI",
            description = "Real-time, forecast, historical, astronomy, weather alerts and time zone data.",
            category = ApiCategory.WEATHER,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.weatherapi.com/docs/",
            testEndpoint = "https://api.weatherapi.com/v1/current.json?key=mock&q=Paris",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("weather", "alerts", "astronomy", "radar")
        ),
        PublicApi(
            id = "7timer",
            name = "7Timer!",
            description = "Astro-weather and meteorological forecast product derived from the NOAA GFS model.",
            category = ApiCategory.WEATHER,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "http://www.7timer.info/doc.php",
            testEndpoint = "https://www.7timer.info/bin/astro.php?lon=113.2&lat=23.1&ac=0&unit=metric&output=json",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("astronomy", "weather", "clouds")
        ),
        PublicApi(
            id = "openweathermap",
            name = "OpenWeatherMap",
            description = "Access current weather data for any location on Earth including over 200,000 cities.",
            category = ApiCategory.WEATHER,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://openweathermap.org/api",
            testEndpoint = "https://api.openweathermap.org/data/2.5/weather?q=Tokyo&appid=demo",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("weather", "popular", "global", "alerts")
        ),

        // FINANCE & CRYPTO (6)
        PublicApi(
            id = "coingecko",
            name = "CoinGecko API",
            description = "Live cryptocurrency prices, volume, market cap, coin info, exchange tickers and historic data.",
            category = ApiCategory.FINANCE,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.coingecko.com/en/api/documentation",
            testEndpoint = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum&vs_currencies=usd",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("crypto", "bitcoin", "prices", "finance", "web3"),
            sampleJs = """const res = await fetch("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd");
const data = await res.json();
console.log(`Bitcoin: ${'$'}${'$'}{data.bitcoin.usd}`);""",
            samplePy = """import requests
res = requests.get("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd")
print("BTC USD:", res.json()["bitcoin"]["usd"])""",
            sampleDart = """import 'package:http/http.dart' as http;
import 'dart:convert';
final res = await http.get(Uri.parse('https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd'));
print(jsonDecode(res.body)['bitcoin']['usd']);"""
        ),
        PublicApi(
            id = "frankfurter",
            name = "Frankfurter Exchange Rates",
            description = "Track foreign exchange reference rates published by the European Central Bank. Fast and free.",
            category = ApiCategory.FINANCE,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.frankfurter.app/docs/",
            testEndpoint = "https://api.frankfurter.app/latest?from=USD&to=EUR,GBP",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("forex", "currencies", "ecb", "rates", "finance")
        ),
        PublicApi(
            id = "coincap",
            name = "CoinCap 2.0",
            description = "Real-time cryptocurrency market data, websocket feeds, exchange rates, and historical candle bars.",
            category = ApiCategory.FINANCE,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://docs.coincap.io/",
            testEndpoint = "https://api.coincap.io/v2/assets/bitcoin",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("crypto", "trading", "finance", "candles")
        ),
        PublicApi(
            id = "exchangerate-api",
            name = "ExchangeRate-API",
            description = "Currency conversion rates for over 160 currencies. Fast, reliable international exchange rates.",
            category = ApiCategory.FINANCE,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.exchangerate-api.com/docs/overview",
            testEndpoint = "https://open.er-api.com/v6/latest/USD",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("currency", "forex", "conversion")
        ),
        PublicApi(
            id = "binance-public",
            name = "Binance Public Market Data",
            description = "Official Binance public spot market ticker, order book depth, and 24hr statistics without auth.",
            category = ApiCategory.FINANCE,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://binance-docs.github.io/apidocs/spot/en/",
            testEndpoint = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("crypto", "binance", "trading", "ticker")
        ),
        PublicApi(
            id = "alphavantage",
            name = "Alpha Vantage",
            description = "Stock market equities, ETFs, mutual funds, cryptocurrency, technical indicators and forex.",
            category = ApiCategory.FINANCE,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.alphavantage.co/documentation/",
            testEndpoint = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=IBM&interval=5min&apikey=demo",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("stocks", "investing", "charts", "technical-analysis")
        ),

        // GAMES (6)
        PublicApi(
            id = "pokeapi",
            name = "PokéAPI",
            description = "The RESTful Pokémon API. All Pokémon data you will ever need in one place, easily accessible.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://pokeapi.co/docs/v2",
            testEndpoint = "https://pokeapi.co/api/v2/pokemon/pikachu",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("pokemon", "nintendo", "gaming", "sprites", "stats"),
            sampleJs = """const res = await fetch("https://pokeapi.co/api/v2/pokemon/charizard");
const data = await res.json();
console.log(data.name, data.types.map(t => t.type.name));""",
            samplePy = """import requests
res = requests.get("https://pokeapi.co/api/v2/pokemon/charizard")
print(res.json()["name"])""",
            sampleDart = """import 'package:http/http.dart' as http;
final res = await http.get(Uri.parse('https://pokeapi.co/api/v2/pokemon/charizard'));
print(res.body);"""
        ),
        PublicApi(
            id = "deck-of-cards",
            name = "Deck of Cards API",
            description = "Simulate a deck of cards: shuffle, draw cards, create piles, and build card games easily.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://deckofcardsapi.com/",
            testEndpoint = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("cards", "blackjack", "poker", "game-engine")
        ),
        PublicApi(
            id = "open-trivia-db",
            name = "Open Trivia DB",
            description = "Free to use, user-contributed trivia database with thousands of categorized quiz questions.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://opentdb.com/api_config.php",
            testEndpoint = "https://opentdb.com/api.php?amount=10",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("trivia", "quiz", "questions", "multiplayer")
        ),
        PublicApi(
            id = "freetogame",
            name = "FreeToGame",
            description = "Database of all free-to-play PC and browser games, MMOs, battle royales, and release trackers.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.freetogame.com/api-doc",
            testEndpoint = "https://www.freetogame.com/api/games",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("gaming", "pc", "free-to-play", "steam")
        ),
        PublicApi(
            id = "cheapshark",
            name = "CheapShark",
            description = "Digital video game deals aggregator. Compare prices across Steam, Epic, GOG, and Humble.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://apidocs.cheapshark.com/",
            testEndpoint = "https://www.cheapshark.com/api/1.0/deals?storeID=1&upperPrice=15",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("deals", "steam", "discounts", "games")
        ),
        PublicApi(
            id = "amiiboapi",
            name = "AmiiboAPI",
            description = "Database of all Nintendo Amiibo figures, cards, game series, and compatibility information.",
            category = ApiCategory.GAMES,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://amiiboapi.com/docs/",
            testEndpoint = "https://www.amiiboapi.com/api/amiibo/?name=mario",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("nintendo", "mario", "amiibo", "collectibles")
        ),

        // MUSIC (5)
        PublicApi(
            id = "itunes-search",
            name = "iTunes Search API",
            description = "Search Apple Music and iTunes store for music, artists, albums, podcasts, movies and 30-sec previews.",
            category = ApiCategory.MUSIC,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/",
            testEndpoint = "https://itunes.apple.com/search?term=daft+punk&limit=5",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("music", "apple", "previews", "audio", "podcasts"),
            sampleJs = """const res = await fetch("https://itunes.apple.com/search?term=coldplay&limit=3");
const data = await res.json();
console.log(data.results.map(r => r.trackName));""",
            samplePy = """import requests
res = requests.get("https://itunes.apple.com/search?term=coldplay&limit=3")
for track in res.json()["results"]:
    print(track["trackName"], track["previewUrl"])""",
            sampleDart = """import 'package:http/http.dart' as http;
final res = await http.get(Uri.parse('https://itunes.apple.com/search?term=coldplay&limit=3'));
print(res.body);"""
        ),
        PublicApi(
            id = "musicbrainz",
            name = "MusicBrainz",
            description = "Open music encyclopedia collecting music metadata: artists, releases, recordings, labels and relationships.",
            category = ApiCategory.MUSIC,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://musicbrainz.org/doc/MusicBrainz_API",
            testEndpoint = "https://musicbrainz.org/ws/2/artist/c8da2e40-3c48-4ba8-8124-4b7e92247ed8?fmt=json",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("metadata", "encyclopedia", "artists", "discography")
        ),
        PublicApi(
            id = "deezer",
            name = "Deezer API",
            description = "Browse Deezer catalog: charts, track search, radio channels, playlists, and audio preview clips.",
            category = ApiCategory.MUSIC,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://developers.deezer.com/api",
            testEndpoint = "https://api.deezer.com/search?q=eminem",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("streaming", "music", "charts", "radio")
        ),
        PublicApi(
            id = "theaudiodb",
            name = "TheAudioDB",
            description = "Community database of audio metadata, high-resolution album artwork, music videos, and artist biographies.",
            category = ApiCategory.MUSIC,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.theaudiodb.com/api_guide.php",
            testEndpoint = "https://www.theaudiodb.com/api/v1/json/2/search.php?s=coldplay",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("artwork", "biographies", "albums", "music-videos")
        ),
        PublicApi(
            id = "genius",
            name = "Genius API",
            description = "Song lyrics database, artist annotations, music intelligence, and culture references.",
            category = ApiCategory.MUSIC,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://docs.genius.com/",
            testEndpoint = "https://api.genius.com/search?q=Kendrick",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("lyrics", "hip-hop", "annotations", "music")
        ),

        // HEALTH (5)
        PublicApi(
            id = "disease-sh",
            name = "disease.sh (Open Disease Data)",
            description = "Global disease data including COVID-19, Influenza, and historical public health metrics.",
            category = ApiCategory.HEALTH,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://disease.sh/docs/",
            testEndpoint = "https://disease.sh/v3/covid-19/all",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("health", "epidemiology", "cdc", "who", "stats")
        ),
        PublicApi(
            id = "openfda",
            name = "openFDA",
            description = "FDA public health data on drugs, medical devices, recall reports, and adverse event warnings.",
            category = ApiCategory.HEALTH,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://open.fda.gov/apis/",
            testEndpoint = "https://api.fda.gov/drug/event.json?limit=1",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("medicine", "fda", "drugs", "recalls")
        ),
        PublicApi(
            id = "nutritionix",
            name = "Nutritionix API",
            description = "Natural language nutrition calculator and restaurant food nutritional facts database.",
            category = ApiCategory.HEALTH,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://developer.nutritionix.com/docs/v2",
            testEndpoint = "https://trackapi.nutritionix.com/v2/search/instant?query=apple",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("calories", "fitness", "nutrition", "diet")
        ),
        PublicApi(
            id = "fitness-calculator",
            name = "Fitness Calculator API",
            description = "Calculate BMI, BMR, daily calorie needs, body fat percentage, and ideal weight targets.",
            category = ApiCategory.HEALTH,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://rapidapi.com/malaaddincelik/api/fitness-calculator",
            testEndpoint = "https://fitness-calculator.p.rapidapi.com/bmi?age=25&weight=70&height=180",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("bmi", "workout", "fitness", "calculator")
        ),
        PublicApi(
            id = "clinical-trials",
            name = "ClinicalTrials.gov API",
            description = "Explore publicly and privately funded clinical studies conducted around the world.",
            category = ApiCategory.HEALTH,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://clinicaltrials.gov/data-api/api",
            testEndpoint = "https://clinicaltrials.gov/api/v2/studies?pageSize=5",
            difficulty = DifficultyLevel.HARD,
            tags = listOf("clinical", "research", "medical", "trials")
        ),

        // ANIMALS (5)
        PublicApi(
            id = "the-cat-api",
            name = "The Cat API",
            description = "Pictures and information about cats and cat breeds. Uploads, voting, and favorites.",
            category = ApiCategory.ANIMALS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://thecatapi.com/",
            testEndpoint = "https://api.thecatapi.com/v1/images/search",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("cats", "pets", "images", "breeds")
        ),
        PublicApi(
            id = "the-dog-api",
            name = "The Dog API",
            description = "Dog pictures, facts, temperament, life expectancy, and detailed breed information.",
            category = ApiCategory.ANIMALS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://thedogapi.com/",
            testEndpoint = "https://api.thedogapi.com/v1/images/search",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("dogs", "pets", "images", "breeds")
        ),
        PublicApi(
            id = "randomfox",
            name = "RandomFox",
            description = "Get curated random pictures of adorable foxes with simple high-speed JSON endpoints.",
            category = ApiCategory.ANIMALS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://randomfox.ca/floof/",
            testEndpoint = "https://randomfox.ca/floof/",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("foxes", "wildlife", "cute", "photos")
        ),
        PublicApi(
            id = "shibe-online",
            name = "Shibe.Online",
            description = "High quality random photos of Shiba Inus, cats, and birds for meme and pet apps.",
            category = ApiCategory.ANIMALS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://shibe.online/",
            testEndpoint = "https://shibe.online/api/shibes?count=1",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("shiba", "doge", "pets", "photos")
        ),
        PublicApi(
            id = "http-cats",
            name = "HTTP Cats",
            description = "Cat images for every HTTP status code (200, 404, 500, etc.). Fun developer debugging asset.",
            category = ApiCategory.ANIMALS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://http.cat/",
            testEndpoint = "https://http.cat/200",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("http", "status-codes", "cats", "humor")
        ),

        // FOOD & DRINK (5)
        PublicApi(
            id = "themealdb",
            name = "TheMealDB",
            description = "Open crowd-sourced database of recipes and meals from around the world with ingredients and videos.",
            category = ApiCategory.FOOD,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.themealdb.com/api.php",
            testEndpoint = "https://www.themealdb.com/api/json/v1/1/random.php",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("recipes", "cooking", "food", "ingredients")
        ),
        PublicApi(
            id = "thecocktaildb",
            name = "TheCocktailDB",
            description = "Database of drinks and cocktails from around the world. Search by ingredient, glass type, or alcohol.",
            category = ApiCategory.FOOD,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.thecocktaildb.com/api.php",
            testEndpoint = "https://www.thecocktaildb.com/api/json/v1/1/random.php",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("cocktails", "bartending", "drinks", "party")
        ),
        PublicApi(
            id = "open-food-facts",
            name = "Open Food Facts",
            description = "Collaborative, free food database containing barcodes, nutritional scores, ingredients and allergens.",
            category = ApiCategory.FOOD,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://world.openfoodfacts.org/data",
            testEndpoint = "https://world.openfoodfacts.org/api/v0/product/737628064500.json",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("barcodes", "groceries", "allergens", "nutrition")
        ),
        PublicApi(
            id = "spoonacular",
            name = "Spoonacular",
            description = "Recipe and food intelligence: search by diet, meal planning, semantic recipe search, and wine pairings.",
            category = ApiCategory.FOOD,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://spoonacular.com/food-api/docs",
            testEndpoint = "https://api.spoonacular.com/recipes/complexSearch?query=pasta&apiKey=mock",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("recipes", "wine-pairing", "meal-planner", "nutrition")
        ),
        PublicApi(
            id = "punk-api",
            name = "Punk API (BrewDog)",
            description = "Search BrewDog's complete craft beer catalog with malt, hops, yeast and food pairing suggestions.",
            category = ApiCategory.FOOD,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://punkapi.com/documentation/v2",
            testEndpoint = "https://api.punkapi.com/v2/beers/random",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("beer", "brewing", "craft-beer", "recipes")
        ),

        // SPORTS (5)
        PublicApi(
            id = "thesportsdb",
            name = "TheSportsDB",
            description = "Crowdsourced sports database with fixtures, team badges, league standings, player biographies and logos.",
            category = ApiCategory.SPORTS,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.thesportsdb.com/api.php",
            testEndpoint = "https://www.thesportsdb.com/api/v1/json/3/all_leagues.php",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("soccer", "nba", "nfl", "scores", "leagues")
        ),
        PublicApi(
            id = "balldontlie",
            name = "balldontlie (NBA)",
            description = "Free NBA stats API: active players, teams, games, live box scores, and season averages.",
            category = ApiCategory.SPORTS,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.balldontlie.io/",
            testEndpoint = "https://api.balldontlie.io/v1/teams",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("nba", "basketball", "box-scores", "stats")
        ),
        PublicApi(
            id = "ergast-f1",
            name = "Jolpica F1 (Ergast Replacement)",
            description = "Historical Formula 1 racing results, driver standings, constructor championships, and circuit data.",
            category = ApiCategory.SPORTS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://jolpica-f1.github.io/jolpica-f1/",
            testEndpoint = "https://api.jolpi.ca/ergast/f1/current/driverStandings.json",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("f1", "racing", "motorsport", "standings")
        ),
        PublicApi(
            id = "football-data",
            name = "Football-Data.org",
            description = "Live scores, tables, squad rosters, and historical match stats for major European football leagues.",
            category = ApiCategory.SPORTS,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.football-data.org/documentation/quickstart",
            testEndpoint = "https://api.football-data.org/v4/competitions/PL/standings",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("premier-league", "champions-league", "soccer")
        ),
        PublicApi(
            id = "chess-com",
            name = "Chess.com Public API",
            description = "Access Chess.com player profiles, grandmaster stats, live club tournaments, and PGN game archives.",
            category = ApiCategory.SPORTS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.chess.com/news/view/published-data-api",
            testEndpoint = "https://api.chess.com/pub/player/hikaru",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("chess", "leaderboards", "pgn", "grandmaster")
        ),

        // BOOKS & LITERATURE (5)
        PublicApi(
            id = "open-library",
            name = "Open Library",
            description = "An open, editable library catalog of millions of books, ISBN search, authors, covers and subjects.",
            category = ApiCategory.BOOKS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://openlibrary.org/developers/api",
            testEndpoint = "https://openlibrary.org/api/books?bibkeys=ISBN:0451526538&format=json&jscmd=data",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("books", "isbn", "reading", "authors", "covers")
        ),
        PublicApi(
            id = "gutendex",
            name = "Gutendex (Project Gutenberg)",
            description = "Web API for Project Gutenberg public domain ebooks: search by topic, author, language, and download EPUB/HTML.",
            category = ApiCategory.BOOKS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://gutendex.com/",
            testEndpoint = "https://gutendex.com/books/?search=dickens",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("ebooks", "classics", "literature", "free-books")
        ),
        PublicApi(
            id = "poetrydb",
            name = "PoetryDB",
            description = "The internet's poetry database: search thousands of classic poems by poet, title, or lines.",
            category = ApiCategory.BOOKS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://github.com/thundercomb/poetrydb",
            testEndpoint = "https://poetrydb.org/author,title/Shakespeare;Sonnet",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("poetry", "shakespeare", "literature", "rhymes")
        ),
        PublicApi(
            id = "bible-api",
            name = "Bible-API",
            description = "Simple REST API to retrieve Bible verses, chapters, and cross-references across multiple translations.",
            category = ApiCategory.BOOKS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://bible-api.com/",
            testEndpoint = "https://bible-api.com/john%203:16",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("bible", "verses", "scripture")
        ),
        PublicApi(
            id = "crossref",
            name = "CrossRef REST API",
            description = "Search over 120 million scholarly research papers, scientific journals, DOIs, and academic citations.",
            category = ApiCategory.BOOKS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://api.crossref.org/swagger-ui/index.html",
            testEndpoint = "https://api.crossref.org/works?rows=2",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("academic", "journals", "doi", "science")
        ),

        // AI & MACHINE LEARNING (5)
        PublicApi(
            id = "gemini-api",
            name = "Google Gemini API",
            description = "State-of-the-art multimodal AI for text, code, reasoning, vision, and function calling.",
            category = ApiCategory.AI,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://ai.google.dev/docs",
            testEndpoint = null,
            difficulty = DifficultyLevel.EASY,
            tags = listOf("llm", "multimodal", "generative-ai", "chat", "code")
        ),
        PublicApi(
            id = "huggingface-inference",
            name = "Hugging Face Serverless Inference",
            description = "Run thousands of open-source AI models for NLP, vision, audio, and classification over HTTP.",
            category = ApiCategory.AI,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://huggingface.co/docs/api-inference/index",
            testEndpoint = null,
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("models", "transformers", "pytorch", "huggingface")
        ),
        PublicApi(
            id = "wit-ai",
            name = "Wit.ai (Meta)",
            description = "Natural language processing platform to parse user intents, entities, speech, and conversational bots.",
            category = ApiCategory.AI,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://wit.ai/docs",
            testEndpoint = null,
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("nlp", "intents", "voice", "bots")
        ),
        PublicApi(
            id = "ollama-local",
            name = "Ollama Local REST API",
            description = "Local developer API running Llama 3, Mistral, and Gemma models locally on localhost:11434.",
            category = ApiCategory.AI,
            auth = AuthType.NONE,
            https = false,
            cors = CorsType.YES,
            docsUrl = "https://github.com/ollama/ollama/blob/main/docs/api.md",
            testEndpoint = "http://localhost:11434/api/tags",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("local-ai", "llama3", "privacy", "offline")
        ),
        PublicApi(
            id = "replicate",
            name = "Replicate API",
            description = "Run open-source machine learning models in the cloud via a simple REST API and pay only for compute.",
            category = ApiCategory.AI,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://replicate.com/docs/reference/http",
            testEndpoint = null,
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("diffusion", "vision", "ai-models", "cloud")
        ),

        // GEOCODING & MAPS (6)
        PublicApi(
            id = "nominatim",
            name = "Nominatim (OpenStreetMap)",
            description = "OpenStreetMap reverse and forward geocoding: convert coordinates to addresses and search locations.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://nominatim.org/release-docs/latest/api/Overview/",
            testEndpoint = "https://nominatim.openstreetmap.org/search?q=1600+Amphitheatre+Parkway&format=json",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("maps", "geocoding", "osm", "addresses", "gps")
        ),
        PublicApi(
            id = "rest-countries",
            name = "REST Countries",
            description = "Information about world countries: currencies, languages, flags, capital cities, borders, and population.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://restcountries.com/",
            testEndpoint = "https://restcountries.com/v3.1/name/japan",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("countries", "flags", "capitals", "currencies", "geography")
        ),
        PublicApi(
            id = "ip-api",
            name = "IP-API (Geolocation)",
            description = "Query IP addresses for country, city, ISP, timezone, coordinates, and zip code in JSON without key.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://ip-api.com/docs",
            testEndpoint = "http://ip-api.com/json/8.8.8.8",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("ip", "geolocation", "isp", "timezone")
        ),
        PublicApi(
            id = "zippopotam",
            name = "Zippopotam.us",
            description = "Zip and postal code lookup across 60+ countries to city, state, latitude, and longitude.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.zippopotam.us/",
            testEndpoint = "https://api.zippopotam.us/us/90210",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("zip-codes", "postal", "address", "shipping")
        ),
        PublicApi(
            id = "bigdatacloud-reverse",
            name = "BigDataCloud Reverse Geocoding",
            description = "Fast, client-side friendly reverse geocoding providing human-readable administrative locality names.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.bigdatacloud.com/free-api/free-reverse-geocode-to-city-api",
            testEndpoint = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=37.42&longitude=-122.08",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("reverse-geocode", "locality", "city", "gps")
        ),
        PublicApi(
            id = "sunrisesunset",
            name = "Sunrise and Sunset Times API",
            description = "Solar calculation engine: provides sunrise, sunset, solar noon, dawn, and dusk for any latitude/longitude.",
            category = ApiCategory.GEOCODING,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://sunrise-sunset.org/api",
            testEndpoint = "https://api.sunrise-sunset.org/json?lat=36.72&lng=-4.42",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("sunrise", "sunset", "astronomy", "time")
        ),

        // NEWS & MEDIA (5)
        PublicApi(
            id = "hackernews-firebase",
            name = "Hacker News Firebase API",
            description = "Official Hacker News real-time database API: top stories, new items, comments, and user profiles.",
            category = ApiCategory.NEWS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://github.com/HackerNews/API",
            testEndpoint = "https://hacker-news.firebaseio.com/v0/topstories.json?limitToFirst=5&orderBy=\"${'$'}key\"",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("tech-news", "y-combinator", "discussions", "real-time")
        ),
        PublicApi(
            id = "reddit-json",
            name = "Reddit JSON Endpoints",
            description = "Public JSON feeds of Reddit subreddits, trending posts, comments and media by appending .json to URLs.",
            category = ApiCategory.NEWS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.reddit.com/dev/api/",
            testEndpoint = "https://www.reddit.com/r/technology/top.json?limit=3",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("reddit", "social-media", "trending", "feed")
        ),
        PublicApi(
            id = "the-guardian-open",
            name = "The Guardian Open Platform",
            description = "Access millions of journalistic articles, headlines, commentary, and cultural pieces from The Guardian.",
            category = ApiCategory.NEWS,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://open-platform.theguardian.com/documentation/",
            testEndpoint = "https://content.guardianapis.com/search?api-key=test",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("journalism", "uk", "world-news", "politics")
        ),
        PublicApi(
            id = "currents-news",
            name = "Currents News API",
            description = "Real-time multilingual breaking world news, filtering by language, category, keywords, and domain.",
            category = ApiCategory.NEWS,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://currentsapi.services/en/docs/",
            testEndpoint = "https://api.currentsapi.services/v1/latest-news?apiKey=demo",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("news", "breaking", "multilingual", "rss")
        ),
        PublicApi(
            id = "spaceflight-news",
            name = "Spaceflight News API (SNAPI)",
            description = "All news related to spaceflight, rocket launches, NASA, SpaceX missions, and planetary exploration.",
            category = ApiCategory.NEWS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://spaceflightnewsapi.net/",
            testEndpoint = "https://api.spaceflightnewsapi.net/v4/articles/?limit=3",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("space", "nasa", "spacex", "launches", "rockets")
        ),

        // EDUCATION & KNOWLEDGE (5)
        PublicApi(
            id = "wikipedia-rest",
            name = "Wikipedia REST API",
            description = "Official Wikimedia API for page summaries, related articles, featured content, and on-this-day events.",
            category = ApiCategory.EDUCATION,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://en.wikipedia.org/api/rest_v1/",
            testEndpoint = "https://en.wikipedia.org/api/rest_v1/page/summary/Computer_science",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("wikipedia", "encyclopedia", "history", "knowledge")
        ),
        PublicApi(
            id = "quotable",
            name = "Quotable",
            description = "Free quotes API with over 2,000 famous quotes from history's greatest philosophers, authors, and leaders.",
            category = ApiCategory.EDUCATION,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://github.com/lukePeavey/quotable",
            testEndpoint = "https://api.quotable.io/random",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("quotes", "inspiration", "philosophy", "motivation")
        ),
        PublicApi(
            id = "numbers-api",
            name = "Numbers API",
            description = "Interesting trivia facts about numbers, math properties, dates, and historical year milestones.",
            category = ApiCategory.EDUCATION,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "http://numbersapi.com/",
            testEndpoint = "http://numbersapi.com/42/math?json",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("math", "trivia", "dates", "numbers")
        ),
        PublicApi(
            id = "universities-hipolabs",
            name = "World Universities (HipoLabs)",
            description = "Search list of universities, colleges, and higher educational institutions across 200+ countries.",
            category = ApiCategory.EDUCATION,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://github.com/Hipo/university-domains-list",
            testEndpoint = "http://universities.hipolabs.com/search?name=Stanford",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("colleges", "universities", "education", "domains")
        ),
        PublicApi(
            id = "bored-api",
            name = "Bored API",
            description = "Find interesting activities and projects to do when bored, categorized by participants, cost, and type.",
            category = ApiCategory.EDUCATION,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.boredapi.com/",
            testEndpoint = "https://bored-api.appbrewery.com/random",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("activities", "hobbies", "ideas", "lifestyle")
        ),

        // OPEN DATA & GOVERNMENT (5)
        PublicApi(
            id = "nasa-open-apis",
            name = "NASA Open APIs",
            description = "Astronomy Picture of the Day (APOD), Mars Rover imagery, Near-Earth Asteroid trackers, and space data.",
            category = ApiCategory.OPEN_DATA,
            auth = AuthType.API_KEY,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://api.nasa.gov/",
            testEndpoint = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("nasa", "space", "astronomy", "mars-rover", "apod")
        ),
        PublicApi(
            id = "world-bank-data",
            name = "World Bank Open Data",
            description = "Global development indicators, GDP, population metrics, poverty data, and international economic stats.",
            category = ApiCategory.OPEN_DATA,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://datahelpdesk.worldbank.org/knowledgebase/topics/12558-developer-information",
            testEndpoint = "https://api.worldbank.org/v2/country/all/indicator/SP.POP.TOTL?format=json&page=1&per_page=5",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("economics", "gdp", "population", "development")
        ),
        PublicApi(
            id = "us-census-bureau",
            name = "US Census Bureau Data API",
            description = "Demographic, economic, geographic and population statistics from US Census surveys and decennial records.",
            category = ApiCategory.OPEN_DATA,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.census.gov/data/developers/data-sets.html",
            testEndpoint = "https://api.census.gov/data/2020/dec/pl?get=NAME&for=state:06",
            difficulty = DifficultyLevel.MEDIUM,
            tags = listOf("demographics", "census", "government", "population")
        ),
        PublicApi(
            id = "uk-police-data",
            name = "UK Police Data API",
            description = "Street-level crime data, outcomes, neighbourhood policing teams, and stop-and-search records across England.",
            category = ApiCategory.OPEN_DATA,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://data.police.uk/docs/",
            testEndpoint = "https://data.police.uk/api/forces",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("crime", "public-safety", "uk", "transparency")
        ),
        PublicApi(
            id = "usgs-earthquakes",
            name = "USGS Earthquake Hazards API",
            description = "Real-time feed of seismic activity, magnitude, epicenter depth, and tsunami alerts worldwide.",
            category = ApiCategory.OPEN_DATA,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://earthquake.usgs.gov/fdsnws/event/1/",
            testEndpoint = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=3",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("earthquakes", "geology", "alerts", "seismic")
        ),

        // DEV TOOLS & TESTING (6)
        PublicApi(
            id = "github-rest",
            name = "GitHub REST API",
            description = "Access public repositories, user profiles, commit history, releases, issues, and star counts.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://docs.github.com/en/rest",
            testEndpoint = "https://api.github.com/users/octocat",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("git", "github", "repositories", "code", "dev")
        ),
        PublicApi(
            id = "jsonplaceholder",
            name = "JSONPlaceholder",
            description = "Free fake REST API for prototyping and testing: posts, comments, albums, photos, todos, and users.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://jsonplaceholder.typicode.com/",
            testEndpoint = "https://jsonplaceholder.typicode.com/posts/1",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("mock", "rest", "prototyping", "posts", "testing")
        ),
        PublicApi(
            id = "reqres",
            name = "ReqRes",
            description = "A hosted REST-API ready to respond to AJAX requests with real response codes and simulated user list.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://reqres.in/",
            testEndpoint = "https://reqres.in/api/users?page=1",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("mock", "ajax", "users", "testing")
        ),
        PublicApi(
            id = "httpbin",
            name = "httpbin.org",
            description = "HTTP client testing service: inspect headers, test methods (GET/POST/PUT/DELETE), status codes, and delays.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://httpbin.org/",
            testEndpoint = "https://httpbin.org/get",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("debugging", "http", "headers", "testing")
        ),
        PublicApi(
            id = "ipify",
            name = "ipify",
            description = "A simple, open-source public IP address API. Get your public IP in plain text or JSON format instantly.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://www.ipify.org/",
            testEndpoint = "https://api.ipify.org?format=json",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("ip", "network", "public-ip", "fast")
        ),
        PublicApi(
            id = "randomuser-me",
            name = "RandomUser.me",
            description = "Free API that generates random user data with avatars, names, addresses, emails and phone numbers.",
            category = ApiCategory.DEV_TOOLS,
            auth = AuthType.NONE,
            https = true,
            cors = CorsType.YES,
            docsUrl = "https://randomuser.me/documentation",
            testEndpoint = "https://randomuser.me/api/",
            difficulty = DifficultyLevel.EASY,
            tags = listOf("dummy-data", "users", "avatars", "placeholders")
        )
    )

    fun getById(id: String): PublicApi? = apis.find { it.id == id }

    val defaultDailyChallenge = DailyProjectChallenge(
        id = "daily-1",
        title = "Mood Weather Music Playlist",
        description = "Combine current weather conditions with Spotify/Apple Music previews to generate dynamic mood playlists.",
        primaryApiId = "open-meteo",
        secondaryApiId = "itunes-search",
        difficulty = DifficultyLevel.EASY,
        estimatedTime = "35 mins"
    )

    val sampleTestimonials = listOf(
        com.example.model.Testimonial(
            author = "Alex Rivera",
            role = "Indie Hacker & Creator",
            quote = "Found the perfect weather & forex APIs for my weekend SaaS in literally 2 minutes. Generated working fetch code immediately.",
            avatarEmoji = "🚀"
        ),
        com.example.model.Testimonial(
            author = "Sarah Chen",
            role = "Senior Frontend Lead",
            quote = "The live ping test saved us from picking an API with terrible latency. APIMatch is bookmarked on all our team machines.",
            avatarEmoji = "⚡"
        ),
        com.example.model.Testimonial(
            author = "Marcus Brody",
            role = "Mobile Dev",
            quote = "The starter code generator in Dart and Python took all the tedious boilerplate away. The Pro watchlist gives immense peace of mind.",
            avatarEmoji = "🎯"
        )
    )
}
