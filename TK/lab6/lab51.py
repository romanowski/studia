import re
import sys

class Process:
	dot = re.compile("(?i)\W*\.\W*")
	eOn = re.compile("(?i)\W*on\W*")
	eOff = re.compile("(?i)\W*off\W*")
	echo =re.compile("(?i)\W*(.*)")
	var = re.compile("%([^%]+)%")
	varP = re.compile("(\w+):(\w+)\=(\w+)")
	varV = re.compile("(\w+):~(\w+),(\w+)")
	
	#forBody= re.compile("(?i)for /l %%(\w+) \((\w+),(\w+),(\w+)\) do \( [^\)]+\)")
	forBody= re.compile("(?i)(for /l %%(\w+) in \(([\w%]+),\W*([\w%]+),\W*([\w%]+)\) do \(([^\)]+)\))")

	forIn = re.compile("\W* (\w+) (\w+) (\w+) (\w+) (\w+)")

	
	setOpt = re.compile("(?i)\W*(\w?)\W*(\w+)\=(.*)")

	gotoP = re.compile("\W*(.+)\ *");

	#choice /C:TNM Idziemy na piwo (Tak, Nie, Moze)?
	choiceOpt = re.compile("(?i)\W*/C:(\w+) (.+)")

	ala= "";
	echoOn = 1;
	beg = "";
	labels ={beg: 0};
	env = {};

	def proces_choice(self, data):

		m = self.choiceOpt.match(data)
		if m :
			opt = m.group(1)
			ans = m.group(2)
			opt = opt




			sel = raw_input("").lower();

			print ans+ " ["+reduce(lambda x, y : x + "," + y, opt) +"]?" + sel.upper();

			opt = opt.lower()


			if sel in opt:
				self.env["ERRORLEVEL"] = str(opt.index(sel)+1)




	def procesEcho(self, data):


		if self.dot.match(data):
			return ""

		if self.eOn.match(data):
			self.echoOn = 1
			return 0

		if self.eOff.match(data):
			self.echoOn = 0
			return 0


		data = self.echo.match(data).group(1);
		return data;





	def proces(self, plik):

		f = open(plik, 'U');

		text = f.read()
		
		for_c = 0;

		for m in self.forBody.findall(text):
			beg = "for i " + m[2] + " " + m[3] + " " + m[4] +" for"+str(for_c) + "\n";
			
			#print beg + m[5].replace("%%", '%') + "\n:for"+str(for_c);
			
			out = ":bfor"+str(for_c)+"\n"+ beg + m[5].replace("%%", '%') + "\ngoto bfor"+str(for_c) + "\n:for"+str(for_c)

		#	print out
			
			text = text.replace(m[0], out);
	
		print text

		lines= text.split("\n");
		ala =0

		pos = 0;

		

		while  pos < len(lines):
		  	line = lines[pos]
		  	if len(line) > 0:
				if(line[0] == ':'):
					self.labels[line[1:]]= pos;
					lines[pos]=""
			pos = pos+1




		pos = 0
		while  pos < len(lines):
		  line = lines[pos]

		
		  if len(line) > 1:
			while line[0] == ' ':
				line = line[1:]
			for m in self.var.findall(line):
			   mat = self.varP.match(m)
			   
			   if mat:
			    line = line.replace("%" + m + "%", self.env[mat.group(1)].replace(mat.group(2), mat.group(3)))
			   else:
			      mat = self.varV.match(m)
			   
			      if mat:
				data = self.env[mat.group(1)][int(mat.group(2)):int(mat.group(3))+1]
				line = line.replace("%" + m + "%", data)
			      else:
				if m in self.env:
				  line = line.replace("%" + m + "%", self.env[m])


			ala = ""
			m = 1;
			ok = 0;
			if line[0] == '@':
				m = 0
				line = line[1:]

			if(line[0] == ':'):
				self.labels[line[1:]]= pos;
				ok = 1
				m = 0

			command = line

			if(self.echoOn and m):
				print  self.beg + "C:\>"+line

			if self.beg == "":
				self.beg = "\n"

			if(line.startswith("echo")):
				ala = self.procesEcho(line[4:])
				ok = 1;


			if(line.startswith("rem")):
				ala = 0
				ok = 1;

			if(line.startswith("choice")):
				self.proces_choice(line[6:])
				ala = 0
				ok = 1;

			if(line.startswith("exit")):
				return ""


			if line.startswith("for"):
				m = self.forIn.match(line[3:])
				
				if(m):	
					var_n = m.group(1);

					var = 0;
					if(not var_n in self.env):
						self.env[var_n] = m.group(2)
						var = int(m.group(2))
					else:
						var = int(self.env[var_n]) + int(m.group(3))

					self.env[var_n] = str(var)
					if var > int(m.group(4)): line = "goto " + m.group(5)
						
					ok = 1
					ala = 0;

				else:
					ok = 0
					ala = 0;

			if(line.startswith("goto")):

				pos = self.labels[self.gotoP.match(line[4:]).group(1)];
				ok = 1
				ala = 0;

			if line.startswith("set"):
			    m = self.setOpt.match(line[3:])
			    if(m):
			      if m.group(1) == "a":
				wyn = eval(m.group(3).replace("\"", ""))			
				self.env[m.group(2)] = str(wyn)
			      else:
				if m.group(1) == "p":
				  wyn = raw_input(m.group(3))
				  
				  #sys.stdout.write(wyn)
				  self.env[m.group(2)] = wyn[0]
				else:
				  self.env[m.group(2)] = m.group(3)



			      ok = 1
			      ala = 0

			if(not ok):
				print "Bad command or file name"
				print line
				ala =0

			if ala != 0:
				print  ala.replace("^", "")
		  pos = pos +1


Process().proces(sys.argv[1])
