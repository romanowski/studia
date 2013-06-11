import re
import sys
 
class Process:
	dot = re.compile("(?i)\W*\.\W*")
	eOn = re.compile("(?i)\W*on\W*")
	eOff = re.compile("(?i)\W*off\W*")
	echo =re.compile("(?i)\W*(.*)")
	var = re.compile("%(.+)%")

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
		
		f = open(plik);
		lines= f.read().split("\n");
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
			for m in self.var.findall(line):
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

			if(line.startswith("goto")):
				
				pos = self.labels[self.gotoP.match(line[4:]).group(1)];
				ok = 1
				ala = 0;
				

			if(not ok):
				print "Bad command or file name"
				ala =0
			
			if ala != 0:
				print  ala
		  pos = pos +1 
		

Process().proces(sys.argv[1])





