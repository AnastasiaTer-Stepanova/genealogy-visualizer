package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.FamilyFilter;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;

import java.util.List;

public interface FamilyRevisionService {

    void delete(Long id);

    FamilyMember getById(Long id);

    FamilyMember save(FamilyMember familyMember);

    FamilyMember update(FamilyMember familyMember);

    List<EasyFamilyMember> filter(FamilyMemberFilter filter);

    List<FamilyMemberFullInfo> getFamilyMemberFullInfoList(FamilyFilter familyFilter);
}
