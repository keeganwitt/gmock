package org.gmock.sample

import org.testng.annotations.Test
import org.testng.annotations.BeforeMethod
import org.gmock.WithGMock

@WithGMock
class ExampleTest {

    ClassUnderTest classUnderTest
    Collaborator mock

    @BeforeMethod void setup() {
        mock = mock(Collaborator)
        classUnderTest = new ClassUnderTest()
        classUnderTest.addListener(mock)
    }

    @Test void removeNonExistingDocument() {
        play {
            classUnderTest.removeDocument("Does not exist")
        }
    }

    @Test void addDocument() {
        mock.documentAdded("New Document")
        play {
            classUnderTest.addDocument("New Document", new byte[0])
        }
    }

    @Test void addAndChangeDocument() {
        mock.documentAdded("Document")
        mock.documentChanged("Document").times(3)
        play {
            4.times {
                classUnderTest.addDocument("Document", new byte[0])
            }
        }
    }

    @Test void voteForRemoval() {
        // expect document addition
        mock.documentAdded("Document")
        // expect to be asked to vote, and vote for it
        mock.voteForRemoval("Document").returns(42)
        // expect document removal
        mock.documentRemoved("Document")

        play {
            classUnderTest.addDocument("Document", new byte[0])
            assert classUnderTest.removeDocument("Document")
        }
    }

    @Test void voteAgainstRemoval() {
        // expect document addition
        mock.documentAdded("Document")
        // expect to be asked to vote, and vote against it
        mock.voteForRemoval("Document").returns(-42)
        // document removal is *not* expected

        play {
            classUnderTest.addDocument("Document", new byte[0])
            assert !classUnderTest.removeDocument("Document")
        }
    }

    @Test void voteForRemovals() {
        with(mock) {
            documentAdded("Document 1")
            documentAdded("Document 2")
            voteForRemovals(["Document 1", "Document 2"] as String[]).returns(42)
            documentRemoved("Document 1")
            documentRemoved("Document 2")
        }
        play {
            classUnderTest.addDocument("Document 1", new byte[0])
            classUnderTest.addDocument("Document 2", new byte[0])
            assert classUnderTest.removeDocuments("Document 1", "Document 2")
        }
    }

    @Test void voteAgainstRemovals() {
        with(mock) {
            documentAdded("Document 1")
            documentAdded("Document 2")
            voteForRemovals(["Document 1", "Document 2"] as String[]).returns(-42)
        }
        play {
            classUnderTest.addDocument("Document 1", new byte[0])
            classUnderTest.addDocument("Document 2", new byte[0])
            assert !classUnderTest.removeDocuments("Document 1", "Document 2")
        }
    }

}
