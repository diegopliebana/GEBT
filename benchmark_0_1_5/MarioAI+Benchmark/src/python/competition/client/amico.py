__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ = "$Feb 24, 2010 1:36:30 PM$"

class AmiCo:
    """
    AmiCo adaptor. Can load and interact with AmiCo library
    """

    def __init__(self, libName = 'AmiCo', libPath = '.'):
        """Documentation"""
        self.libName = libName
        self.libPath = libPath
        self.loadAmiCoLibrary()

    def __del__(self):
        self.sock.close()

    def loadAmiCoLibrary(self):
        pass

    def recvData(self):
        """receive arbitrary data """
        return ""


    def sendData(self, data):
        """send arbitrary string """
        pass
