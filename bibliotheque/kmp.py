def kmp(facteur,retenue,text):
    i = 0
    j = 0
    text = addPadding(text)
    while i < len(text):
        if j == len(facteur):
            return i-len(facteur)
        if text[i] == facteur[j]:
            i +=1
            j +=1
        else:
            if retenue[j] == -1:
                i +=1
                j = 0
            else:
                j = retenue[j]
    return -1

def addPadding(text):
    return text+' '

def getRetenue(facteur):
    first_letter = facteur[0]
    retenue = [0]* len(facteur)
    prefix = []
    i = 0
    while i< len(facteur) :
        if facteur[i] == first_letter:
            retenue[i] = -1
        else:
            for pre in prefix:
                len_pre = len(pre)
                suffix = ""
                
                k = i-len_pre
                while k < i :
                    suffix += facteur[k]
                    k +=1

                p = suffix + facteur[i]

                if prefix.__contains__(p):
                    retenue[i] = 0
                elif prefix.__contains__(suffix):
                    retenue[i] = len_pre
        
        s= ""
        j = 0
        while j<i:
            s+= facteur[j]
            j +=1

        prefix.append(s)
        i +=1

    return retenue