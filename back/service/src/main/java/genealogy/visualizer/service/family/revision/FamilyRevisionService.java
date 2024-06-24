package genealogy.visualizer.service.family.revision;

import genealogy.visualizer.api.model.EasyFamilyMember;
import genealogy.visualizer.api.model.FamilyFilter;
import genealogy.visualizer.api.model.FamilyMember;
import genealogy.visualizer.api.model.FamilyMemberFilter;
import genealogy.visualizer.api.model.FamilyMemberFullInfo;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

import java.util.List;

public interface FamilyRevisionService extends CrudService<FamilyMember>, FilterService<EasyFamilyMember, FamilyMemberFilter> {

    List<FamilyMemberFullInfo> getFamilyMemberFullInfoList(FamilyFilter familyFilter);
}
