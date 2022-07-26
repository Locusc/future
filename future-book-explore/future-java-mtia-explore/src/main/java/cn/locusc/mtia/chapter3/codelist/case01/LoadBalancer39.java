package cn.locusc.mtia.chapter3.codelist.case01;

/**
 * @author Jay
 * 满足要求一
 * 2022/7/4
 */
public interface LoadBalancer39 {

    void updateCandidate(final Candidate candidate);

    Endpoint nextEndpoint();

}
