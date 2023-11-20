package uk.gov.moj.sdt.domain.visitor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.visitor.Tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AbstractDomainObjectVisitorTest extends AbstractSdtUnitTestBase {

    private AbstractDomainObjectVisitor abstractDomainObjectVisitor;

    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception thrown";
    private static final String EXPECTED_MESSAGE =
            "Missing visitor implementation: this method should never be called.";
    private static final String UNEXPECTED_EXCEPTION_MESSAGE =
            "Exception has unexpected message";

    @Mock
    Tree mockTree;

    @Override
    protected void setUpLocalTests() {
        abstractDomainObjectVisitor = new AbstractDomainObjectVisitor() {};
    }

    @Test
    void testVisitBulkCustomerThrows() {
        IBulkCustomer mockBulkCustomer = mock(IBulkCustomer.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockBulkCustomer, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitBulkSubmissionThrows() {
        IBulkSubmission mockBulkSubmission = mock(IBulkSubmission.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockBulkSubmission, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitServiceTypeThrows() {
        IServiceType mockServiceType = mock(IServiceType.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockServiceType, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitTargetApplicationThrows() {
        ITargetApplication mockTargetApplication = mock(ITargetApplication.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockTargetApplication, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitIndividualRequestThrows() {
        IIndividualRequest mockIndividualRequest = mock(IIndividualRequest.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockIndividualRequest, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitSubmitQueryRequestThrows() {
        ISubmitQueryRequest mockSubmitQueryRequest = mock(ISubmitQueryRequest.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockSubmitQueryRequest, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitBulkFeedbackRequestThrows() {
        IBulkFeedbackRequest mockBulkFeedbackRequest = mock(IBulkFeedbackRequest.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockBulkFeedbackRequest, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @Test
    void testVisitErrorLogThrows() {
        IErrorLog mockErrorLog = mock(IErrorLog.class);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                     () -> abstractDomainObjectVisitor.visit(mockErrorLog, mockTree), UNEXPECTED_EXCEPTION);
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), UNEXPECTED_EXCEPTION_MESSAGE);
    }

}
