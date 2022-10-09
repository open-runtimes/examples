import requests
import json
import yfinance as yf

def main(req, res):
	payload = json.loads(req.payload)
	currency = payload.get('type')
	try:
		if currency in ['bitcoin','gold','google','amazon','silver', 'ethereum']:
			if currency == 'bitcoin':
				ticker = 'BTC-USD'
			elif currency == 'gold':
				ticker = 'GC=F'
			elif currency == 'google':
				ticker = 'GOOG'
			elif currency == 'amazon':
				ticker = 'AMZN'
			elif currency == 'silver':
				ticker = 'SI=F'
			elif currency == 'ethereumn':
				ticker = 'ETH-USD'
			stock_info = yf.Ticker(ticker=ticker).get_info
			price = stock_info['regularMarketPrice']
			return res.json({'success': True, 'price': price})
	except:
		return res.json({'success': False, "message":"Type is not supported."})
