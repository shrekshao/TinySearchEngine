from collections import OrderedDict

f = open('keywordlist.txt')
f_out = open('sortbyidf.txt', 'w')
dic = {}
for line in f:
	tokens = line.split('\t')
	dic[tokens[0]] = float(tokens[1])

ordered = OrderedDict(sorted(dic.items(), key=lambda t: t[1]))
for key, value in ordered.iteritems():
	if not key.isalpha():
		continue
	f_out.write(key)
	f_out.write('\t')
	f_out.write(str(value))
	f_out.write('\n')

