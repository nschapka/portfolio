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
    # init arrays
    chunks = []
    chunkTemp = []
    for line in lines:
        if line != '\n':                # pulling lines until we reach a blank line - which should just be a newLine char
            chunkTemp.append(line)
        else:                           # otherwise we terminate the chunk and reset the temp array
            if chunkTemp != []:         # conditioning against whitespace at the end of the file, or extra lines between chunks
                chunks.append(chunkTemp)
            chunkTemp = []

    return chunks # as an array of string arrays


with open('C:\\users\\nschapka\\desktop\\mediainfoOut.txt', "r", encoding="utf-8") as f:
    lines = []
    # pull the whole text in at once
    for line in f:
        lines.append(line)


# for each chunk given by getChunks we grab a title as a key, and a subDict as a value, populated from each following line of the chunk
thisDict = {title(chunk): {keys(line): vals(line) for line in chunk[1:len(chunk)+1]} for chunk in getChunks(lines)}

# test outputs
print(thisDict)
print(thisDict['General']['Format'])
