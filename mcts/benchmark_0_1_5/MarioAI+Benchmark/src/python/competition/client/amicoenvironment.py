__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ = "$Feb 24, 2010 1:32:30 PM$"

from environment import Environment
from amico import AmiCo

class AmiCoEnvironment(Environment):
    verbose = False
    def __init__(self, agentName = "UnnamedClient"):
        """General AmiCo Environment"""
        if self.verbose:
            print "AmiCoENV: agentName ", agentName
        # Init and Load MAIBe here
        self.initialized = True
        self.amico = AmiCo() # load with default name and path
        
    def isAvailable(self):
        """returns the availability status of the environment"""
        return self.initialized

    def getSensors(self):
        """ receives an observation via tcp connection"""
        #        print "Looking forward to receive data"

        data = self.amico.recvData()
        return data

    def performAction(self, action):
        """takes a numpy array of ints and sends it to amico"""
        self.client.sendData(action)
