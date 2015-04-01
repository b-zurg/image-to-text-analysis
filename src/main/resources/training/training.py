import os
import subprocess
import shutil
from subprocess import call
import re
import argparse

parser = argparse.ArgumentParser(description='This is a Script that automates font training with Tesseract')

parser.add_argument("-t", "--ttfdir", help="directory name for .ttf files", required=True)
parser.add_argument("-d", "--homedir", help="home directory for project", required =False, default="./")

args = parser.parse_args()

ttfdir = args.homedir+args.ttfdir+"/"




class TessTrainer:
	# def __init__(self):


	def getActualFontNames(self, fontdir):
		out = subprocess.Popen("text2image --list_available_fonts --fonts_dir="+fontdir, stdout = subprocess.PIPE, stderr = subprocess.STDOUT, shell=True).communicate()[0]
		raw = out.decode("utf-8").splitlines()
		if len(raw) == 2:
			raw = raw[1]
		elif len(raw) == 1:
			raw = raw[0]

		regex = re.compile(r"^ *\d*: ")
		regex2 = re.compile(r",")
		processed = re.sub(regex, "", raw).lstrip().rstrip()
		return processed;

	def getFontFiles(self):
		fileNames = []
		extensions = []
		for file in os.listdir(ttfdir):
			fileName, fileExtension = os.path.splitext(file)
			if fileExtension.lower() == ".ttf" or fileExtension.lower() == ".otf":
				fileNames.append(fileName);
				extensions.append(fileExtension)
		return fileNames, extensions


	def createDirInHome(self, directory):
		if not os.path.exists(args.homedir+directory):
			os.makedirs(args.homedir+directory)

	def makeFontDirs(self, fontfs):
		dic = {}
		self.createDirInHome("fontfolders")
		for filename in fontfs :
			self.createDirInHome("fontfolders/"+filename)
			self.createDirInHome("fontfolders/"+filename+"/final")
			dic[filename] = args.homedir+"fontfolders/"+filename+"/"
		return dic

	def moveFontsToDirs(self, filenames, extensions, filedirdict):
		for i in range(len(filenames)):
			fontfile  = filenames[i] + extensions[i]
			destdir = filedirdict[filenames[i]]

			shutil.copyfile(ttfdir+fontfile, destdir+fontfile)
			shutil.copyfile(args.homedir+"training_text.txt", destdir+"training_text.txt")
			shutil.copyfile(args.homedir+"font_properties", destdir+"font_properties")

	def runProcessForFonts(self, fontdict) :
		for fontname in fontdict:
			print("\n\n\n~~~~~~~~~~~~~~~~"+fontname+"~~~~~~~~~~~~~~~~~~")
			fontdir = fontdict[fontname]
			actualfontname = self.getActualFontNames(fontdir)
			actualfontnameNoSpaces = self.replaceSpacesWith(actualfontname, "")

			text2imagecmd = self.createText2ImageCmd(actualfontname)	
			trainingcmd   = self.createTrainingCmd 	(actualfontnameNoSpaces)
			unicharsetcmd = self.createUnicharsetCmd(actualfontnameNoSpaces)
			mftrainingcmd = self.createMFTrainingCmd(actualfontnameNoSpaces)
			cntrainingcmd = self.createCNTrainingCmd(actualfontnameNoSpaces)
			
			print("-----\nINFO: Calling text2image")
			print(text2imagecmd)
			call(text2imagecmd, cwd=fontdir, shell=True)

			print("-----\nINFO: Calling tesseract training")
			print(trainingcmd)
			call(trainingcmd,   cwd=fontdir, shell=True)

			print("-----\nINFO: Calling unicharset creator")
			print(unicharsetcmd)
			call(unicharsetcmd, cwd=fontdir, shell=True)

			print("-----\nINFO: Calling mftraining")
			print(mftrainingcmd)
			call(mftrainingcmd, cwd=fontdir, shell=True)
			
			print("-----\nINFO: Calling cntraining")
			print(cntrainingcmd)
			call(cntrainingcmd, cwd=fontdir, shell=True)

			self.renameFiles(fontdir, actualfontname)
			self.finishCombine(fontdir, actualfontname)
			self.moveFinishedToFinal(fontdir, actualfontname)

	def replaceSpacesWith(self, string, replace):
		regex = re.compile(r" ")
		return re.sub(regex, replace, string)

	def createText2ImageCmd(self, actualfontname):
			command = "text2image"
			opt1 = " --text=training_text.txt "
			opt2 = " --outputbase=eng."+self.replaceSpacesWith(actualfontname, "")+".exp0 "
			opt3 = " --font='"+actualfontname+"' "
			opt4 = " --fonts_dir=./"
			final = command + opt1 + opt2 + opt3 + opt4
			return final

	def createTrainingCmd(self, fontnameNoSpaces):
		command = "tesseract"
		opt1 = " eng."+fontnameNoSpaces+".exp0.tif " 
		opt2 = " eng."+fontnameNoSpaces+".exp0 "
		opt3 = " box.train.stderr "
		final = command + opt1 + opt2 + opt3
		return final

	def createUnicharsetCmd(self, fontnameNoSpaces):
		command = "unicharset_extractor"
		opt1 = " eng."+fontnameNoSpaces+".exp0.box "
		final = command + opt1
		return final

	def createMFTrainingCmd(self, fontnameNoSpaces):
			command = "mftraining"
			opt1 = " -F font_properties "
			opt2 = " -U unicharset "
			opt3 = " -O eng.unicharset "
			opt4 = " eng."+fontnameNoSpaces+".exp0.tr"
			final = command + opt1 + opt2 + opt3 + opt4
			return final

	def createCNTrainingCmd(self, actualfontname): 
		command = "cntraining"
		opt1 = " eng."+self.replaceSpacesWith(actualfontname, "")+".exp0.tr "
		final = command+opt1
		return final

	def renameFiles(self, workingdir, actualfontname):
		name = self.replaceSpacesWith(actualfontname, "")
		os.rename(workingdir+"shapetable", workingdir+"final/"+name+".shapetable")
		os.rename(workingdir+"normproto", workingdir+"final/"+name+".normproto")
		os.rename(workingdir+"inttemp", workingdir+"final/"+name+".inttemp")
		os.rename(workingdir+"pffmtable", workingdir+"final/"+name+".pffmtable")
		os.rename(workingdir+"unicharset", workingdir+"final/"+name+".unicharset")

	def finishCombine(self, workingdir, actualfontname):
		cmd = "combine_tessdata "+self.replaceSpacesWith(actualfontname, "")+"."
		call(cmd, cwd=workingdir+"final/", shell=True)

	def moveFinishedToFinal(self, workingdir, actualfontname):
		filename = self.replaceSpacesWith(actualfontname, "")+".traineddata"
		shutil.copyfile(workingdir+"final/"+filename, args.homedir+"final/"+filename)



tt = TessTrainer()

fileNames, fileExtensions = tt.getFontFiles()
fileDirDict = tt.makeFontDirs(fileNames)
tt.moveFontsToDirs(fileNames, fileExtensions, fileDirDict)
tt.createDirInHome("final")
tt.runProcessForFonts(fileDirDict)


# print ("HOME DIR: \t\t" + args.homedir)
# print ("TTFDIR: \t\t" + args.ttfdir)
# print ("EXAMPLE FONT DIR: \t" + fileDirDict["ARIAL"])


# fontFiles = getFontFiles()

# fontDirectories = makeFontDirs(fontFiles)

# moveFontsToDirs(fontDirectories)

# runProcessForFonts(fontDirectories)

# createDirInHome("final")




print("~~~~~~~~")
print("done")
print("~~~~~~~~")
