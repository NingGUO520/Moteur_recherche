import kmp
import re

class Coord:
    
    def __init__(self,line,occ):
        self.line = line
        self.occ = occ 

    def __str__(self):
        return "("+str(self.line)+","+str(self.occ)+")"

class IndexingTable:

    unsorted = dict()
    index = dict()
    arr_line = []
    
    def __init__(self,text):
        self.arr_line = text.splitlines()
        line_number = 1
        for l in self.arr_line:
            if l != "":
                arr_words = re.split("[^a-zA-Z'-]",l)
                for w in arr_words:
                    if w and len(w)>2:
                        cmp = 0
                        text = []
                        facteur = w
                        retenue = kmp.getRetenue(facteur)
                        if cmp == 0:
                           text = l
                        else:
                           text = l[:cmp]
                        occ = kmp.kmp(facteur,retenue,text)

                        if occ != -1 and w not in self.unsorted:
                            list = []
                            list.append(Coord(line_number,occ))
                            self.unsorted[w] = list
                        elif occ != -1:
                            self.unsorted.get(w).append(Coord(line_number,occ+1+cmp))
                        cmp += occ
            line_number =+1

    def getIndexTable(self):
        return self.unsorted
    

class Edge:

    def __init__(self,targetNode,label):
        self.targetNode = targetNode
        self.label = label
    
    def __init__(self,label):
        self.label = label
        self.targetNode = Node()
    

class Node:

    def __init__(self,edges,coords):
        self.edges = edges
        self.coords = coords
    
    def __init__(self,coords):
        self.edges = []
        self.coords = coords
    
     def __init__(self):
        self.edges = []
        self.coords = []

    def isLeaf(self):
        return not self.edges

class RadixTree:

    def __init__(self,root,index):
        self.index = index.getIndexTable()
        self.root = Node()

    def __init__(self,index):
        self.index = index

    def construct(self):
        for key,value in self.index:
            addElement(self.root,key,value)

    def addElement(self,root,key,value):
        if(not root.edges)

    