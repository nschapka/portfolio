import re

def title(chunk):
    # this should be the fist line of the chunk:
    return chunk[0].strip()


def keys(line):
    # pull a key from a line
    return re.search('.+?(?=:)', line).group(0).strip()


def vals(line):
    # pull a value from a line
    return re.search('(?<=:).+', line).group(0).strip()


def getChunks(lines):
    chunks = []
    chunkTemp = []
    for line in lines:
        if line != '\n':
            chunkTemp.append(line)
        else:
            if chunkTemp != []:
                chunks.append(chunkTemp)
            chunkTemp = []

    return chunks


with open('C:\\users\\nschapka\\desktop\\mediainfoOut.txt', "r", encoding="utf-8") as f:
    lines = []
    for line in f:
        lines.append(line)


thisDict = {title(chunk): {keys(line): vals(line) for line in chunk[1:len(chunk)+1]} for chunk in getChunks(lines)}

print(thisDict)
print(thisDict['General']['Format'])
